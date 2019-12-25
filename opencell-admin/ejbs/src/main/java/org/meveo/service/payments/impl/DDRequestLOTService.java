/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SepaDirectDebitAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.script.payment.AccountOperationFilterScript;

/**
 * The Class DDRequestLOTService.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@Stateless
public class DDRequestLOTService extends PersistenceService<DDRequestLOT> {

    /**
     * The dd request item service.
     */
    @Inject
    private DDRequestItemService ddRequestItemService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private SepaDirectDebitAsync sepaDirectDebitAsync;

    /**
     * Creates the DDRequest lot.
     *
     * @param ddrequestLotOp   the ddrequest lot op
     * @param ddRequestBuilder direct debit request builder
     * @param result           the result
     * @return the DD request LOT
     * @throws BusinessEntityException the business entity exception
     * @throws Exception               the exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DDRequestLOT createDDRequestLot(DDRequestLotOp ddrequestLotOp, DDRequestBuilder ddRequestBuilder, JobExecutionResultImpl result)
            throws BusinessEntityException, Exception {

        try {
            BigDecimal totalAmount = BigDecimal.ZERO;
            DDRequestLOT ddRequestLOT = new DDRequestLOT();
            ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
            ddRequestLOT.setSendDate(new Date());
            ddRequestLOT.setPaymentOrRefundEnum(ddrequestLotOp.getPaymentOrRefundEnum());
            ddRequestLOT.setSeller(ddrequestLotOp.getSeller());
            ddRequestLOT.setTotalAmount(totalAmount);
            ddRequestLOT.setNbItemsKo(0);
            ddRequestLOT.setNbItemsOk(0);
            ddRequestLOT.setRejectedCause("");
            this.create(ddRequestLOT);
            return ddRequestLOT;
        } catch (Exception e) {
            log.error("Failed to create direct debit request lot for id {}", ddrequestLotOp.getId(), e);
            ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
            ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
            result.registerError(ddrequestLotOp.getId(), e.getMessage());
            result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());
            return null;
        }

    }

    //    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void generateDDRquestLotFile(DDRequestLOT ddRequestLOT, final DDRequestBuilderInterface ddRequestBuilderInterface, Provider appProvider, int nbRuns) throws Exception {
        ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
        ddRequestLOT.setFileName(ddRequestBuilderInterface.getDDFileName(ddRequestLOT, appProvider));
        ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOT, appProvider, nbRuns);
        ddRequestLOT.setSendDate(new Date());
        update(ddRequestLOT);
    }

    public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT) throws Exception {
        createPaymentsOrRefundsForDDRequestLot(ddRequestLOT, 1L, 0L, null);
    }

    /**
     * Creates the payments or refunds for DD request lot.
     *
     * @param ddRequestLOT the dd request LOT
     * @throws Exception
     */
    public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT, Long nbRuns, Long waitingMillis, JobExecutionResultImpl result) throws Exception {
        ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
        log.info("createPaymentsForDDRequestLot ddRequestLotId: {}, size:{}", ddRequestLOT.getId(), ddRequestLOT.getDdrequestItems().size());
        if (ddRequestLOT.isPaymentCreated()) {
            throw new BusinessException("Payment Already created.");
        }

        SubListCreator<DDRequestItem> subListCreator = new SubListCreator<>(ddRequestLOT.getDdrequestItems(), nbRuns.intValue());
        List<Future<String>> futures = new ArrayList<>();
        while (subListCreator.isHasNext()) {
            futures.add(sepaDirectDebitAsync.launchAndForgetPaymentCreation(subListCreator.getNextWorkSet(), result));
            try {
                Thread.sleep(waitingMillis);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                // It was cancelled from outside - no interest
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (result != null) {
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                }
                log.error("Failed to execute async method", cause);
            }
        }
        ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
        ddRequestLOT.setPaymentCreated(true);
        update(ddRequestLOT);
        log.info("Successful createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());

    }

    /**
     * Reject payment.
     *
     * @param ddRequestItem the dd request item
     * @param rejectCause   the reject cause
     * @throws BusinessException the business exception
     */
    public void rejectPayment(DDRequestItem ddRequestItem, String rejectCause, String fileName) throws BusinessException {
        if (ddRequestItem.getRejectedFileName() != null) {
            log.warn("DDRequestItem already rejected.");
            return;
        }
        ddRequestItem.setRejectedFileName(fileName);
        AccountOperation automatedPaymentorRefund = null;
        if (ddRequestItem.getAutomatedPayment() != null) {
            automatedPaymentorRefund = ddRequestItem.getAutomatedPayment();
        } else {
            automatedPaymentorRefund = ddRequestItem.getAutomatedRefund();
        }
        if (automatedPaymentorRefund == null || automatedPaymentorRefund.getMatchingAmounts() == null || automatedPaymentorRefund.getMatchingAmounts().isEmpty()) {
            throw new BusinessException("ddRequestItem id :" + ddRequestItem.getId() + " Callback not expected");
        }
        paymentService.paymentCallback(automatedPaymentorRefund.getReference(), PaymentStatusEnum.REJECTED, rejectCause, rejectCause);
    }

    /**
     * Process reject file.
     *
     * @param ddRejectFileInfos the dd reject file infos
     * @throws BusinessException the business exception
     */
    public void processRejectFile(DDRejectFileInfos ddRejectFileInfos) throws BusinessException {
        DDRequestLOT dDRequestLOT = null;
        if (ddRejectFileInfos.getDdRequestLotId() != null) {
            dDRequestLOT = findById(ddRejectFileInfos.getDdRequestLotId(), Arrays.asList("ddrequestItems"));
        }
        if (dDRequestLOT != null) {
            if (ddRejectFileInfos.isTheDDRequestFileWasRejected()) {
                // original message rejected at protocol level control
                CopyOnWriteArrayList<DDRequestItem> items = new CopyOnWriteArrayList<>(dDRequestLOT.getDdrequestItems());
                for (DDRequestItem ddRequestItem : items) {
                    if (!ddRequestItem.hasError()) {
                        rejectPayment(ddRequestItem, "RJCT", ddRejectFileInfos.getFileName());
                    }
                }
                dDRequestLOT.setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
            }
            dDRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());
        }
        for (Entry<Long, String> entry : ddRejectFileInfos.getListInvoiceRefsRejected().entrySet()) {
            DDRequestItem ddRequestItem = ddRequestItemService.findById(entry.getKey(), Arrays.asList("ddRequestLOT"));
            if (ddRequestItem == null) {
                throw new BusinessException("Cant find item by id:" + entry.getKey());
            }

            rejectPayment(ddRequestItem, entry.getValue(), ddRejectFileInfos.getFileName());
            ddRequestItem.getDdRequestLOT().setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
            ddRequestItem.getDdRequestLOT().setReturnFileName(ddRejectFileInfos.getFileName());
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DDRequestLOT> createDdRequestLotWithItems(JobExecutionResultImpl result, DDRequestBuilder ddRequestBuilder, DDRequestLotOp ddrequestLotOp,
            AccountOperationFilterScript aoFilterScript, Long nbRuns, Long waitingMillis, DDRequestBuilderInterface ddRequestBuilderInterface, int limit) throws Exception {
        List<Future<DDRequestLOT>> futures = new ArrayList<>();
        List<Long> listAoToPayIds = ddRequestBuilderInterface.findListAoToPayIds(ddrequestLotOp, limit);
        SubListCreator<Long> subListCreator = new SubListCreator<>(listAoToPayIds, nbRuns.intValue());
        log.info("{} AOs, divided into {} sublist", listAoToPayIds.size(), nbRuns.intValue());
        while (subListCreator.isHasNext()) {
            futures.add(sepaDirectDebitAsync.launchAndForgetDDRequestItemsCreation(ddRequestBuilder, subListCreator.getNextWorkSet(), appProvider, aoFilterScript, ddrequestLotOp));

            if (subListCreator.isHasNext()) {
                try {
                    Thread.sleep(waitingMillis);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }
        List<DDRequestLOT> items = new ArrayList<>();
        log.info("waiting for all DDR items to be created");
        for (Future<DDRequestLOT> future : futures) {
            try {
                DDRequestLOT ddRLotPart = future.get();
                items.add(ddRLotPart);
            } catch (InterruptedException e) {
                // should ask why is interrupted
                log.error("this future was interrupted because ", e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                result.registerError(cause.getMessage());
                log.error("Failed to execute async method", cause);
            }
        }
        return items;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DDRequestLOT updateDDRLot(DDRequestLOT ddRequestLot, List<DDRequestLOT> ddRequestLots) {
        log.info("Update DDR lot, from {} threads", ddRequestLots.size());
        List<Long> itemIds = new ArrayList<>();
        for (DDRequestLOT lot : ddRequestLots) {
            itemIds.addAll(lot.getDdrequestItems().parallelStream().map(DDRequestItem::getId).collect(Collectors.toList()));
            ddRequestLot.setNbItemsOk(ddRequestLot.getNbItemsOk() + lot.getNbItemsOk());
            ddRequestLot.setNbItemsKo(ddRequestLot.getNbItemsKo() + lot.getNbItemsKo());
            ddRequestLot.setTotalAmount(ddRequestLot.getTotalAmount().add(lot.getTotalAmount()));
            String rejectedCause = StringUtils.concat(ddRequestLot.getRejectedCause(), lot.getRejectedCause());
            ddRequestLot.setRejectedCause(StringUtils.truncate(rejectedCause, 255, true));
        }
        update(ddRequestLot);
        ddRequestItemService.updateDDRequestItems(itemIds, ddRequestLot.getId());
        log.info("Creating DDR lot with total amount {} ", ddRequestLot.getTotalAmount());
        return ddRequestLot;
    }
}
