package org.meveo.admin.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.DDRequestBuilderFactory;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.meveo.service.script.payment.DateRangeScript;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * The Class SepaDirectDebitJobBean.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@Stateless
public class SepaDirectDebitJobBean extends BaseJobBean {

    /**
     * Number of AOs id to process in a single job run
     */
    private static final int PROCESS_ROWS_IN_JOB_RUN = 2_000_000;
    /**
     * The log.
     */
    @Inject
    private Logger log;

    /**
     * The d D request lot op service.
     */
    @Inject
    private DDRequestLotOpService dDRequestLotOpService;

    /**
     * The d D request LOT service.
     */
    @Inject
    private DDRequestLOTService dDRequestLOTService;

    /**
     * The job execution service.
     */
    @Inject
    private JobExecutionService jobExecutionService;

    /**
     * The dd request builder service.
     */
    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    /**
     * The dd request builder factory.
     */
    @Inject
    private DDRequestBuilderFactory ddRequestBuilderFactory;

    /**
     * The seller service.
     */
    @Inject
    private SellerService sellerService;

    /**
     * The app provider.
     */
    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /**
     * The script instance service.
     */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Execute.
     *
     * @param result      the result
     * @param jobInstance the job instance
     */
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        try {
            Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "SepaJob_nbRuns", -1L);
            Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "SepaJob_waitingMillis", (long) Runtime.getRuntime().availableProcessors());

            DDRequestBuilder ddRequestBuilder;
            String paymentOrRefundCF = (String) this.getParamOrCFValue(jobInstance, "SepaJob_paymentOrRefund", PaymentOrRefundEnum.PAYMENT);
            PaymentOrRefundEnum paymentOrRefundEnum = PaymentOrRefundEnum.valueOf(paymentOrRefundCF.toUpperCase());
            EntityReferenceWrapper ddRequestBuilderCF = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_ddRequestBuilder");
            if (ddRequestBuilderCF != null) {
                String ddRequestBuilderCode = (ddRequestBuilderCF).getCode();
                ddRequestBuilder = ddRequestBuilderService.findByCode(ddRequestBuilderCode);
                if (ddRequestBuilder == null) {
                    throw new BusinessException("Can't find ddRequestBuilder by code:" + ddRequestBuilderCode);
                }
            } else {
                throw new BusinessException("Can't find ddRequestBuilder in CFs");
            }

            EntityReferenceWrapper sellerCF = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_seller");
            Seller seller = null;
            if (sellerCF != null) {
                String sellerCode = (sellerCF).getCode();
                seller = sellerService.findByCode(sellerCode);
            }

            AccountOperationFilterScript aoFilterScript = this.getAOScriptInstance(jobInstance);

            DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);

            List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(ddRequestBuilder, seller, paymentOrRefundEnum);

            if (CollectionUtils.isEmpty(ddrequestOps)) {
                final String msg = "ddrequestOps IS EMPTY !";
                log.info(msg);
                result.setNbItemsToProcess(0);
                result.registerWarning(msg);
                return;
            }
            log.info("ddrequestOps found:" + ddrequestOps.size());
            result.setNbItemsToProcess(ddrequestOps.size());

            for (DDRequestLotOp ddrequestLotOp : ddrequestOps) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                try {
                    DateRangeScript dateRangeScript = this.getDueDateRangeScript(ddrequestLotOp);
                    if (dateRangeScript != null) { // computing custom due date range :
                        this.updateOperationDateRange(ddrequestLotOp, dateRangeScript);
                    }
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
                        // create DDR lot
                        DDRequestLOT ddRequestLot = dDRequestLOTService.createDDRequestLot(ddrequestLotOp, ddRequestBuilder, result);

                        List<DDRequestLOT> lots = dDRequestLOTService
                                .createDdRequestLotWithItems(result, ddRequestBuilder, ddrequestLotOp, aoFilterScript, nbRuns, waitingMillis,
                                        ddRequestBuilderInterface, PROCESS_ROWS_IN_JOB_RUN);
                        ddRequestLot = dDRequestLOTService.updateDDRLot(ddRequestLot, lots);
                        if (ddRequestLot != null) {
                            dDRequestLOTService.generateDDRquestLotFile(ddRequestLot, ddRequestBuilderInterface, appProvider);
                            result.addReport(ddRequestLot.getRejectedCause());
                            dDRequestLOTService.createPaymentsOrRefundsForDDRequestLot(ddRequestLot, nbRuns, waitingMillis, result);
                            if (isEmpty(ddRequestLot.getRejectedCause())) {
                                result.registerSucces();
                            }
                        }
                    }
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.PAYMENT) {
                        dDRequestLOTService.createPaymentsOrRefundsForDDRequestLot(ddrequestLotOp.getDdrequestLOT(), nbRuns, waitingMillis, result);
                        result.registerSucces();
                    }
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
                        dDRequestLOTService.generateDDRquestLotFile(ddrequestLotOp.getDdrequestLOT(), ddRequestBuilderInterface, appProvider);
                        result.registerSucces();
                    }
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
                    dDRequestLotOpService.update(ddrequestLotOp);
                    if (BooleanUtils.isTrue(ddrequestLotOp.getRecurrent())) {
                        this.createNewDdrequestLotOp(ddrequestLotOp);
                    }
                } catch (Exception e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
                    if (BooleanUtils.isTrue(ddrequestLotOp.getRecurrent())) {
                        this.createNewDdrequestLotOp(ddrequestLotOp);
                    }
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
                    ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
                    dDRequestLotOpService.update(ddrequestLotOp);
                    result.registerError(ddrequestLotOp.getId(), e.getMessage());
                    result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to sepa direct debit", e);
        }
    }

    /**
     * Update operation date range.
     *
     * @param ddrequestLotOp  the ddrequest lot op
     * @param dateRangeScript the date range script
     */
    private void updateOperationDateRange(DDRequestLotOp ddrequestLotOp, DateRangeScript dateRangeScript) {
        try {
            DateRange dueDateRange = dateRangeScript.computeDateRange(new HashMap<>()); // no addtional params are needed right now for computeDateRange, may be in the future.
            // Due date from :
            Date fromDueDate = dueDateRange.getFrom();
            if (fromDueDate == null) {
                fromDueDate = new Date(1);
            }
            ddrequestLotOp.setFromDueDate(fromDueDate);

            // Due date to :
            Date toDueDate = dueDateRange.getTo();
            if (toDueDate == null) {
                toDueDate = DateUtils.addYearsToDate(fromDueDate, 1000);
            }
            ddrequestLotOp.setToDueDate(toDueDate);
        } catch (Exception e) {
            log.error("Error on updateOperationDateRange {} ", e.getMessage(), e);
        }
    }

    /**
     * Gets the due date range script.
     *
     * @param ddrequestLotOp the ddrequest lot op
     * @return the due date range script
     */
    private DateRangeScript getDueDateRangeScript(DDRequestLotOp ddrequestLotOp) {
        try {
            ScriptInstance scriptInstance = ddrequestLotOp.getScriptInstance();
            if (scriptInstance != null) {
                scriptInstance = scriptInstanceService.retrieveIfNotManaged(scriptInstance);
                final String scriptCode = scriptInstance.getCode();
                if (scriptCode != null) {
                    log.debug(" looking for ScriptInstance with code :  [{}] ", scriptCode);
                    ScriptInterface si = scriptInstanceService.getScriptInstance(scriptCode);
                    if (si != null && si instanceof DateRangeScript) {
                        return (DateRangeScript) si;
                    }
                }
            }
        } catch (Exception e) {
            log.error(" Error on getDueDateRangeScript : [{}]", e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new DDRequestLotOp instance, using the initial one's informations. <br>
     * Hence a recurrent job could treat the expected invoices permanently.
     *
     * @param ddrequestLotOp the ddrequest lot op
     */
    private void createNewDdrequestLotOp(DDRequestLotOp ddrequestLotOp) {
        try {
            DDRequestLotOp newDDRequestLotOp = new DDRequestLotOp();
            newDDRequestLotOp.setPaymentOrRefundEnum(ddrequestLotOp.getPaymentOrRefundEnum());
            newDDRequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);

            newDDRequestLotOp.setRecurrent(true);
            newDDRequestLotOp.setSeller(ddrequestLotOp.getSeller());
            ScriptInstance dueDateRange = ddrequestLotOp.getScriptInstance();
            newDDRequestLotOp.setScriptInstance(dueDateRange);
            if (dueDateRange == null) {
                newDDRequestLotOp.setFromDueDate(ddrequestLotOp.getFromDueDate());
                newDDRequestLotOp.setToDueDate(ddrequestLotOp.getToDueDate());
            }
            newDDRequestLotOp.setDdRequestBuilder(ddrequestLotOp.getDdRequestBuilder());
            newDDRequestLotOp.setFilter(ddrequestLotOp.getFilter());
            newDDRequestLotOp.setDdrequestOp(ddrequestLotOp.getDdrequestOp());

            this.dDRequestLotOpService.create(newDDRequestLotOp);
        } catch (Exception e) {
            log.error(" error on createNewDdrequestLotOp {} ", e.getMessage(), e);
        }
    }

    /**
     * Gets the AO script instance.
     *
     * @param jobInstance the job instance
     * @return the AO script instance
     */
    private AccountOperationFilterScript getAOScriptInstance(JobInstance jobInstance) {
        try {
            String aoFilterScriptCode = null;
            EntityReferenceWrapper entityReferenceWrapper = ((EntityReferenceWrapper) getParamOrCFValue(jobInstance, "SepaJob_aoFilterScript"));
            if (entityReferenceWrapper != null) {
                aoFilterScriptCode = entityReferenceWrapper.getCode();
            }

            if (aoFilterScriptCode != null) {
                log.debug(" looking for ScriptInstance with code :  [{}] ", aoFilterScriptCode);
                ScriptInterface si = scriptInstanceService.getScriptInstance(aoFilterScriptCode);
                if (si instanceof AccountOperationFilterScript) {
                    return (AccountOperationFilterScript) si;
                }
            }
        } catch (Exception e) {
            log.error(" Error on newAoFilterScriptInstance : [{}]", e.getMessage());
        }
        return null;
    }

    private class PageResult {
        private long lastId;
        private boolean moreToProcess;

        public PageResult(long lastId, boolean moreToProcess) {
            this.lastId = lastId;
            this.moreToProcess = moreToProcess;
        }

        public long getLastId() {
            return lastId;
        }

        public boolean isMoreToProcess() {
            return moreToProcess;
        }

        public void setLastId(Long lastId) {
            this.lastId = lastId;
        }

        public void setMoreToProcess(boolean moreToProcess) {
            this.moreToProcess = moreToProcess;
        }
    }

}
