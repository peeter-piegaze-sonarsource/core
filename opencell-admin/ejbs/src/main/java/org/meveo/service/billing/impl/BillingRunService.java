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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.meveo.admin.async.AmountsToInvoice;
import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.order.OrderService;

/**
 * The Class BillingRunService.
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Tien Lan PHUNG
 * @author Abdelmounaim Akadid
 * @lastModifiedVersion 7.0.0
 * 
 */
@Stateless
public class BillingRunService extends PersistenceService<BillingRun> {

    /** The wallet operation service. */
    @Inject
    private WalletOperationService walletOperationService;

    /** The billing account service. */
    @Inject
    private BillingAccountService billingAccountService;

    /** The rated transaction service. */
    @Inject
    private RatedTransactionService ratedTransactionService;

    /** The resource messages. */
    @Inject
    private ResourceBundle resourceMessages;

    /** The invoicing async. */
    @Inject
    private InvoicingAsync invoicingAsync;

    /** The invoice service. */
    @Inject
    private InvoiceService invoiceService;

    /** The billing run extension service. */
    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    /** The service singleton. */
    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    /**
     * Generate pre invoicing reports.
     *
     * @param billingRun the billing run
     * @return the pre invoicing reports DTO
     * @throws BusinessException the business exception
     */
    public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.debug("start generatePreInvoicingReports.......");

        PreInvoicingReportsDTO preInvoicingReportsDTO = new PreInvoicingReportsDTO();

        preInvoicingReportsDTO.setBillingCycleCode(billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getCode() : null);
        preInvoicingReportsDTO.setBillingAccountNumber(billingRun.getBillingAccountNumber());
        preInvoicingReportsDTO.setLastTransactionDate(billingRun.getLastTransactionDate());
        preInvoicingReportsDTO.setInvoiceDate(billingRun.getInvoiceDate());
        preInvoicingReportsDTO.setBillableBillingAccountNumber(billingRun.getBillableBillingAcountNumber());
        preInvoicingReportsDTO.setAmoutWitountTax(billingRun.getPrAmountWithoutTax());

        BillingCycle billingCycle = billingRun.getBillingCycle();

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();
        endDate = endDate != null ? endDate : new Date();
        List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

        if (billingCycle != null) {
            billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
        } else {
            String[] baIds = billingRun.getSelectedBillingAccounts().split(",");
            for (String id : Arrays.asList(baIds)) {
                Long baId = Long.valueOf(id);
                billingAccounts.add(billingAccountService.findById(baId));
            }
        }

        log.debug("BA in PreInvoicingReport: {}", billingAccounts.size());
        Integer checkBANumber = 0;
        Integer directDebitBANumber = 0;
        Integer tipBANumber = 0;
        Integer wiretransferBANumber = 0;
        Integer creditDebitCardBANumber = 0;

        Integer checkBillableBANumber = 0;
        Integer directDebitBillableBANumber = 0;
        Integer tipBillableBANumber = 0;
        Integer wiretransferBillableBANumber = 0;
        Integer creditDebitCardBillableBANumber = 0;

        BigDecimal checkBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal directDebitBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal tipBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal wiretransferBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal creditDebitCardBillableBAAmountHT = BigDecimal.ZERO;

        for (BillingAccount billingAccount : billingAccounts) {

            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }
            // avoiding NPE
            if (paymentMethodEnum != null) {
                switch (paymentMethodEnum) {
                case CHECK:
                    checkBANumber++;
                    break;
                case DIRECTDEBIT:
                    directDebitBANumber++;
                    break;
                case WIRETRANSFER:
                    wiretransferBANumber++;
                    break;

                case CARD:
                    creditDebitCardBANumber++;
                    break;

                default:
                    break;
                }
            }

        }

        List<BillingAccount> listBA = getEntityManager().createNamedQuery("BillingAccount.PreInv", BillingAccount.class).setParameter("billingRunId", billingRun.getId())
            .getResultList();

        for (BillingAccount billingAccount : listBA) {
            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }

            // avoiding NPE
            if (paymentMethodEnum != null) {
                switch (paymentMethodEnum) {
                case CHECK:
                    checkBillableBANumber++;
                    checkBillableBAAmountHT = checkBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;
                case DIRECTDEBIT:
                    directDebitBillableBANumber++;
                    directDebitBillableBAAmountHT = directDebitBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;
                case WIRETRANSFER:
                    wiretransferBillableBANumber++;
                    wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;

                case CARD:
                    creditDebitCardBillableBANumber++;
                    creditDebitCardBillableBAAmountHT = creditDebitCardBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());

                default:
                    break;
                }
            }
        }

        preInvoicingReportsDTO.setCheckBANumber(checkBANumber);
        preInvoicingReportsDTO.setCheckBillableBAAmountHT(round(checkBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCheckBillableBANumber(checkBillableBANumber);
        preInvoicingReportsDTO.setDirectDebitBANumber(directDebitBANumber);
        preInvoicingReportsDTO.setDirectDebitBillableBAAmountHT(round(directDebitBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setDirectDebitBillableBANumber(directDebitBillableBANumber);
        preInvoicingReportsDTO.setTipBANumber(tipBANumber);
        preInvoicingReportsDTO.setTipBillableBAAmountHT(round(tipBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setTipBillableBANumber(tipBillableBANumber);
        preInvoicingReportsDTO.setWiretransferBANumber(wiretransferBANumber);
        preInvoicingReportsDTO.setWiretransferBillableBAAmountHT(round(wiretransferBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setWiretransferBillableBANumber(wiretransferBillableBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBANumber(creditDebitCardBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBillableBAAmountHT(round(creditDebitCardBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCreditDebitCardBillableBANumber(creditDebitCardBillableBANumber);

        return preInvoicingReportsDTO;
    }

    /**
     * Generate post invoicing reports.
     *
     * @param billingRun the billing run
     * @return the post invoicing reports DTO
     * @throws BusinessException the business exception
     */
    public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.info("generatePostInvoicingReports billingRun=" + billingRun.getId());
        PostInvoicingReportsDTO postInvoicingReportsDTO = new PostInvoicingReportsDTO();

        BigDecimal globalAmountHT = BigDecimal.ZERO;
        BigDecimal globalAmountTTC = BigDecimal.ZERO;

        Integer positiveInvoicesNumber = 0;
        BigDecimal positiveInvoicesAmountHT = BigDecimal.ZERO;
        BigDecimal positiveInvoicesAmount = BigDecimal.ZERO;
        BigDecimal positiveInvoicesTaxAmount = BigDecimal.ZERO;

        Integer negativeInvoicesNumber = 0;
        BigDecimal negativeInvoicesAmountHT = BigDecimal.ZERO;
        BigDecimal negativeInvoicesTaxAmount = BigDecimal.ZERO;
        BigDecimal negativeInvoicesAmount = BigDecimal.ZERO;

        Integer emptyInvoicesNumber = 0;
        Integer electronicInvoicesNumber = 0;

        Integer checkInvoicesNumber = 0;
        Integer directDebitInvoicesNumber = 0;
        Integer tipInvoicesNumber = 0;
        Integer wiretransferInvoicesNumber = 0;
        Integer creditDebitCardInvoicesNumber = 0;
        Integer npmInvoicesNumber = 0;

        BigDecimal checkAmuontHT = BigDecimal.ZERO;
        BigDecimal directDebitAmuontHT = BigDecimal.ZERO;
        BigDecimal tipAmuontHT = BigDecimal.ZERO;
        BigDecimal wiretransferAmuontHT = BigDecimal.ZERO;
        BigDecimal creditDebitCardAmountHT = BigDecimal.ZERO;
        BigDecimal npmAmountHT = BigDecimal.ZERO;

        BigDecimal checkAmuont = BigDecimal.ZERO;
        BigDecimal directDebitAmuont = BigDecimal.ZERO;
        BigDecimal tipAmuont = BigDecimal.ZERO;
        BigDecimal wiretransferAmuont = BigDecimal.ZERO;
        BigDecimal creditDebitCardAmount = BigDecimal.ZERO;
        BigDecimal npmAmount = BigDecimal.ZERO;

        List<Invoice> invoices = getEntityManager().createNamedQuery("Invoice.byBr", Invoice.class).setParameter("billingRunId", billingRun.getId()).getResultList();

        for (Invoice invoice : invoices) {

            if ((invoice.getAmountWithoutTax() != null) && (invoice.getAmountWithTax() != null) && (invoice.getPaymentMethodType() != null)) {
                switch (invoice.getPaymentMethodType()) {
                case CHECK:
                    checkInvoicesNumber++;
                    checkAmuontHT = checkAmuontHT.add(invoice.getAmountWithoutTax());
                    checkAmuont = checkAmuont.add(invoice.getAmountWithTax());
                    break;
                case DIRECTDEBIT:
                    directDebitInvoicesNumber++;
                    directDebitAmuontHT = directDebitAmuontHT.add(invoice.getAmountWithoutTax());
                    directDebitAmuont = directDebitAmuont.add(invoice.getAmountWithTax());
                    break;
                case WIRETRANSFER:
                    wiretransferInvoicesNumber++;
                    wiretransferAmuontHT = wiretransferAmuontHT.add(invoice.getAmountWithoutTax());
                    wiretransferAmuont = wiretransferAmuont.add(invoice.getAmountWithTax());
                    break;
                case CARD:
                    // check if card is expired
                    if ((invoice.getPaymentMethod() != null) && invoice.getPaymentMethod().isExpired()) {
                        npmInvoicesNumber++;
                        npmAmountHT = npmAmountHT.add(invoice.getAmountWithoutTax());
                        npmAmount = npmAmount.add(invoice.getAmountWithTax());
                    } else {
                        creditDebitCardInvoicesNumber++;
                        creditDebitCardAmountHT = creditDebitCardAmountHT.add(invoice.getAmountWithoutTax());
                        creditDebitCardAmount = creditDebitCardAmount.add(invoice.getAmountWithTax());
                    }
                    break;

                default:
                    break;
                }
            }

            if ((invoice.getAmountWithoutTax() != null) && (invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0)) {
                positiveInvoicesNumber++;
                positiveInvoicesAmountHT = positiveInvoicesAmountHT.add(invoice.getAmountWithoutTax());
                positiveInvoicesTaxAmount = positiveInvoicesTaxAmount.add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax());
                positiveInvoicesAmount = positiveInvoicesAmount.add(invoice.getAmountWithTax());
            } else if ((invoice.getAmountWithoutTax() == null) || (invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) == 0)) {
                emptyInvoicesNumber++;
            } else {
                negativeInvoicesNumber++;
                negativeInvoicesAmountHT = negativeInvoicesAmountHT.add(invoice.getAmountWithoutTax());
                negativeInvoicesTaxAmount = negativeInvoicesTaxAmount.add(invoice.getAmountTax());
                negativeInvoicesAmount = negativeInvoicesAmount.add(invoice.getAmountWithTax());
            }

            if (invoice.getBillingAccount().getElectronicBilling()) {
                electronicInvoicesNumber++;
            }

            if ((invoice.getAmountWithoutTax() != null) && (invoice.getAmountWithTax() != null)) {
                globalAmountHT = globalAmountHT.add(invoice.getAmountWithoutTax());
                globalAmountTTC = globalAmountTTC.add(invoice.getAmountWithTax());
            }

        }

        postInvoicingReportsDTO.setInvoicesNumber(invoices.size());
        postInvoicingReportsDTO.setCheckAmuont(checkAmuont);
        postInvoicingReportsDTO.setCheckAmuontHT(checkAmuontHT);
        postInvoicingReportsDTO.setCheckInvoicesNumber(checkInvoicesNumber);
        postInvoicingReportsDTO.setDirectDebitAmuont(directDebitAmuont);
        postInvoicingReportsDTO.setDirectDebitAmuontHT(directDebitAmuontHT);
        postInvoicingReportsDTO.setDirectDebitInvoicesNumber(directDebitInvoicesNumber);
        postInvoicingReportsDTO.setElectronicInvoicesNumber(electronicInvoicesNumber);
        postInvoicingReportsDTO.setEmptyInvoicesNumber(emptyInvoicesNumber);

        postInvoicingReportsDTO.setPositiveInvoicesAmountHT(positiveInvoicesAmountHT);
        postInvoicingReportsDTO.setPositiveInvoicesAmount(positiveInvoicesAmount);
        postInvoicingReportsDTO.setPositiveInvoicesTaxAmount(positiveInvoicesTaxAmount);
        postInvoicingReportsDTO.setPositiveInvoicesNumber(positiveInvoicesNumber);

        postInvoicingReportsDTO.setNegativeInvoicesAmountHT(negativeInvoicesAmountHT);
        postInvoicingReportsDTO.setNegativeInvoicesAmount(negativeInvoicesAmount);
        postInvoicingReportsDTO.setNegativeInvoicesTaxAmount(negativeInvoicesTaxAmount);
        postInvoicingReportsDTO.setNegativeInvoicesNumber(negativeInvoicesNumber);

        postInvoicingReportsDTO.setTipAmuont(tipAmuont);
        postInvoicingReportsDTO.setTipAmuontHT(tipAmuontHT);
        postInvoicingReportsDTO.setTipInvoicesNumber(tipInvoicesNumber);

        postInvoicingReportsDTO.setWiretransferAmuont(wiretransferAmuont);
        postInvoicingReportsDTO.setWiretransferAmuontHT(wiretransferAmuontHT);
        postInvoicingReportsDTO.setWiretransferInvoicesNumber(wiretransferInvoicesNumber);

        postInvoicingReportsDTO.setCreditDebitCardAmount(creditDebitCardAmount);
        postInvoicingReportsDTO.setCreditDebitCardAmountHT(creditDebitCardAmountHT);
        postInvoicingReportsDTO.setCreditDebitCardInvoicesNumber(creditDebitCardInvoicesNumber);

        postInvoicingReportsDTO.setNpmAmount(npmAmount);
        postInvoicingReportsDTO.setNpmAmountHT(npmAmountHT);
        postInvoicingReportsDTO.setNpmInvoicesNumber(npmInvoicesNumber);

        postInvoicingReportsDTO.setGlobalAmount(globalAmountHT);

        return postInvoicingReportsDTO;
    }

    /**
     * Round.
     *
     * @param amount the amount
     * @param decimal the decimal
     * @return the big decimal
     */
    public static BigDecimal round(BigDecimal amount, int decimal) {
        if (amount == null) {
            return null;
        }
        amount = amount.setScale(decimal, RoundingMode.HALF_UP);

        return amount;
    }

    @Asynchronous
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> cancelAsync(Long billingRunId) {
        try {

            BillingRun billingRun = findById(billingRunId);
            int count = 10;
            // We will wait until we get a billingRun instance with status Cancelling
            while ((billingRun != null) && (count > 0) && !billingRun.getStatus().equals(BillingRunStatusEnum.CANCELLING)) {
                try {
                    Thread.sleep((10 - count) * 1000);
                } catch (InterruptedException e) {
                    log.warn("Warning on thread sleep={}", e.getMessage());
                }
                refresh(billingRun);
                log.info("BillingRun {} has status {}. COUNT:{}.", billingRunId, billingRun.getStatus(), count);
                count--;
            }
            if (billingRun == null) {
                throw new BusinessException("Cannot instantiate a billingRun instance with id :" + billingRunId);
            }
            if (!billingRun.getStatus().equals(BillingRunStatusEnum.CANCELLING)) {
                log.info("BillingRun {} has status {}.", billingRunId, billingRun.getStatus());
                throw new BusinessException("BillingRun instance status " + billingRun.getStatus() + " with id :" + billingRunId);
            }

            cancelBillingRun(billingRun);

        } catch (BusinessException e) {
            log.error("Error cancelling a billing run with id={}. {}", billingRunId, e.getMessage());
        }
        return new AsyncResult<>("OK");
    }

    /**
     * Mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any invoices and invoice aggregates that were created during
     * invoicing process
     *
     * @param billingRun The billing run
     * @throws BusinessException Business exception
     */
    private void cleanBillingRun(BillingRun billingRun) throws BusinessException {

        ratedTransactionService.deleteMinRTs(billingRun);
        ratedTransactionService.uninvoiceRTs(billingRun);
        invoiceService.deleteInvoices(billingRun);

        // Andrius: I see no point of this update, as it has no relevant change:
        // Query queryBA = getEntityManager().createQuery("update " + BillingAccount.class.getName() + " set billingRun=null where billingRun=:billingRun");
        // queryBA.setParameter("billingRun", billingRun);
        // queryBA.executeUpdate();
    }

    /**
     * Checks if is active billing runs exist.
     *
     * @return true, if is active billing runs exist
     */
    @SuppressWarnings("unchecked")
    public boolean isActiveBillingRunsExist() {
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c");
        qb.startOrClause();
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.NEW);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREVALIDATED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.POSTINVOICED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREINVOICED);
        qb.endOrClause();
        List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

        return (billingRuns != null) && (billingRuns.size() > 0) ? true : false;
    }

    /**
     * Gets the billing runs.
     *
     * @param status the status
     * @return the billing runs
     */
    public List<BillingRun> getbillingRuns(BillingRunStatusEnum... status) {
        return getBillingRuns(null, status);
    }

    /**
     * Gets the billing runs.
     *
     * @param code the code
     * @param status the status
     * @return the billing runs
     */
    @SuppressWarnings("unchecked")
    public List<BillingRun> getBillingRuns(String code, BillingRunStatusEnum... status) {

        BillingRunStatusEnum bRStatus;
        log.debug("getbillingRuns ");
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null);

        if (code != null) {
            qb.addCriterion("c.billingCycle.code", "=", code, false);
        }

        qb.startOrClause();
        if (status != null) {
            for (int i = 0; i < status.length; i++) {
                bRStatus = status[i];
                qb.addCriterionEnum("c.status", bRStatus);
            }
        }
        qb.endOrClause();

        List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

        return billingRuns;
    }

    /**
     * Gets entities.
     *
     * @param billingRun the billing run
     * @return the entity objects
     */
    private List<? extends IBillableEntity> getEntitiesToInvoice(BillingRun billingRun) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        if (billingCycle != null) {

            Date startDate = billingRun.getStartDate();
            Date endDate = billingRun.getEndDate();

            if ((startDate != null) && (endDate == null)) {
                endDate = new Date();
            }

            if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
                return subscriptionService.findSubscriptions(billingCycle, startDate, endDate);
            }

            if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
                return orderService.findOrders(billingCycle, startDate, endDate);
            }

            return billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);

        } else {
            List<BillingAccount> result = new ArrayList<BillingAccount>();
            String[] baIds = billingRun.getSelectedBillingAccounts().split(",");

            for (String id : Arrays.asList(baIds)) {
                // Long baId = Long.valueOf(id);
                // result.add(baId);
                result.add(billingAccountService.findById(new Long(id)));
            }
            return result;
        }
    }

    /**
     * Gets entities that are associated with a billing run
     *
     * @param billingRun the billing run
     * @return the entity objects
     */
    private List<? extends IBillableEntity> getEntitiesByBillingRun(BillingRun billingRun) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
            return subscriptionService.findSubscriptions(billingRun);
        }

        if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
            return orderService.findOrders(billingRun);
        }

        return billingAccountService.findBillingAccounts(billingRun);

    }

    /**
     * Creates the agregates and invoice.
     * 
     * @param billingRun the billing run
     * @param nbRuns the nb runs
     * @param waitingMillis the waiting millis
     * @param jobInstanceId the job instance id
     * @throws BusinessException the business exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId) throws BusinessException {

        List<? extends IBillableEntity> entities = getEntitiesToInvoice(billingRun);

        SubListCreator<IBillableEntity> subListCreator = null;

        try {
            subListCreator = new SubListCreator(entities, (int) nbRuns);
        } catch (Exception e1) {
            throw new BusinessException("cannot create  agregates and invoice with nbRuns=" + nbRuns);
        }

        boolean[] minRTsUsed = ratedTransactionService.isMinRTsUsed();

        List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
        MeveoUser lastCurrentUser = currentUser.unProxy();
        while (subListCreator.isHasNext()) {
            asyncReturns.add(invoicingAsync.createAgregatesAndInvoiceAsync(subListCreator.getNextWorkSet(), billingRun, jobInstanceId, minRTsUsed[0], minRTsUsed[1], minRTsUsed[2],
                lastCurrentUser));
            try {
                Thread.sleep(waitingMillis);
            } catch (InterruptedException e) {
                log.error("Failed to create agregates and invoice waiting for thread", e);
                throw new BusinessException(e);
            }
        }
        for (Future<String> futureItsNow : asyncReturns) {
            try {
                futureItsNow.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to create agregates and invoice getting future", e);
                throw new BusinessException(e);
            }
        }

    }

    /**
     * Creates the agregates and invoice.
     * 
     * @param billingRun billing run
     * @param nbRuns nb of runs
     * @param waitingMillis waiting millis
     * @param jobInstanceId the job instance id
     * @param entities list of entities
     * @param instantiateMinRtsForService Should rated transactions to reach minimum invoicing amount be checked and instantiated on service level.
     * @param instantiateMinRtsForSubscription Should rated transactions to reach minimum invoicing amount be checked and instantiated on subscription level.
     * @param instantiateMinRtsForBA Should rated transactions to reach minimum invoicing amount be checked and instantiated on Billing account level.
     * @throws BusinessException business exception.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, List<? extends IBillableEntity> entities,
            boolean instantiateMinRtsForService, boolean instantiateMinRtsForSubscription, boolean instantiateMinRtsForBA) throws BusinessException {
        SubListCreator subListCreator = null;
        try {
            subListCreator = new SubListCreator(entities, (int) nbRuns);
        } catch (Exception e1) {
            throw new BusinessException("cannot create agregates and invoice with nbRuns=" + nbRuns);
        }

        List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
        MeveoUser lastCurrentUser = currentUser.unProxy();
        while (subListCreator.isHasNext()) {
            asyncReturns.add(invoicingAsync.createAgregatesAndInvoiceAsync(subListCreator.getNextWorkSet(), billingRun, jobInstanceId, instantiateMinRtsForService,
                instantiateMinRtsForSubscription, instantiateMinRtsForBA, lastCurrentUser));
            try {
                Thread.sleep(waitingMillis);
            } catch (InterruptedException e) {
                log.error("Failed to create agregates and invoice waiting for thread", e);
                throw new BusinessException(e);
            }
        }
        for (Future<String> futureItsNow : asyncReturns) {
            try {
                futureItsNow.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to create agregates and invoice getting future", e);
                throw new BusinessException(e);
            }
        }
    }

    /**
     * Assign invoice number and increment BA invoice dates.
     *
     * @param billingRun The billing run
     * @param nbRuns the nb runs
     * @param waitingMillis The waiting millis
     * @param jobInstanceId The job instance id
     * @param result the Job execution result
     * @throws BusinessException the business exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void assignInvoiceNumberAndIncrementBAInvoiceDates(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, JobExecutionResultImpl result)
            throws BusinessException {
        List<InvoicesToNumberInfo> invoiceSummary = invoiceService.getInvoicesToNumberSummary(billingRun.getId());
        // Reserve invoice number for each invoice type/seller/invoice date combination
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            InvoiceSequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
                invoicesToNumberInfo.getInvoiceDate(), invoicesToNumberInfo.getNrOfInvoices());
            invoicesToNumberInfo.setNumberingSequence(sequence);
        }

        // Find and process invoices
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            List<Long> invoices = invoiceService.getInvoiceIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
                invoicesToNumberInfo.getInvoiceDate());
            // Validate that what was retrieved as summary matches the details
            if (invoices.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
                throw new BusinessException(
                    String.format("Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s", invoices.size(), invoicesToNumberInfo.getNrOfInvoices(),
                        billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate()));
            }

            SubListCreator subListCreator = null;

            try {
                subListCreator = new SubListCreator(invoices, (int) nbRuns);
            } catch (Exception e1) {
                throw new BusinessException("Failed to subdivide an invoice list with nbRuns=" + nbRuns);
            }

            List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                asyncReturns.add(invoicingAsync.assignInvoiceNumberAndIncrementBAInvoiceDatesAsync(billingRun, subListCreator.getNextWorkSet(), invoicesToNumberInfo, jobInstanceId,
                    result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis);
                } catch (InterruptedException e) {
                    log.error("Failed to create agregates and invoice waiting for thread", e);
                    throw new BusinessException(e);
                }
            }
            for (Future<String> futureItsNow : asyncReturns) {
                try {
                    futureItsNow.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Failed to create agregates and invoice getting future", e);
                    throw new BusinessException(e);
                }
            }
        }
    }

    /**
     * Launch exceptional invoicing.
     *
     * @param billingAccountIds the billing account ids
     * @param invoiceDate the invoice date
     * @param lastTransactionDate the last transaction date
     * @param processType the process type
     * @return the billing run
     * @throws BusinessException the business exception
     */
    public BillingRun launchExceptionalInvoicing(List<Long> billingAccountIds, Date invoiceDate, Date lastTransactionDate, BillingProcessTypesEnum processType)
            throws BusinessException {
        log.info("launchExceptionelInvoicing...");

        ParamBean param = paramBeanFactory.getInstance();
        String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
        boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
        log.info("launchInvoicing allowManyInvoicing=#", isAllowed);
        if (isActiveBillingRunsExist() && !isAllowed) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.alreadyLunched"));
        }

        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        billingRun.setProcessType(processType);
        String selectedBillingAccounts = "";
        String sep = "";
        boolean isBillable = false;

        if (lastTransactionDate == null) {
            lastTransactionDate = new Date();
        }

        BillingAccount currentBA = null;
        for (Long baId : billingAccountIds) {
            currentBA = billingAccountService.findById(baId);
            if (currentBA == null) {
                throw new BusinessException("BillingAccount whit id=" + baId + " does not exists");
            }
            selectedBillingAccounts = selectedBillingAccounts + sep + baId;
            sep = ",";
            if (!isBillable && ratedTransactionService.isBillingAccountBillable(currentBA, null, lastTransactionDate)) {
                isBillable = true;
            }
        }

        if (!isBillable) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
        }
        log.debug("selectedBillingAccounts=" + selectedBillingAccounts);
        billingRun.setSelectedBillingAccounts(selectedBillingAccounts);

        billingRun.setInvoiceDate(invoiceDate);
        billingRun.setLastTransactionDate(lastTransactionDate);
        create(billingRun);
        commit();
        return billingRun;
    }

    /**
     * Invoicing process for the billingRun, launched by invoicingJob.
     *
     * @param billingRun the billing run to process
     * @param nbRuns the nb runs
     * @param waitingMillis the waiting millis
     * @param jobInstanceId the job instance
     * @param result the Job execution result
     * @throws Exception the exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void validate(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, JobExecutionResultImpl result) throws Exception {
        log.info("Processing billingRun id={} status={}", billingRun.getId(), billingRun.getStatus());

        List<IBillableEntity> billableEntities = new ArrayList<>();

        BillingCycle billingCycle = billingRun.getBillingCycle();
        BillingEntityTypeEnum type = null;
        if (billingCycle != null) {
            type = billingCycle.getType();
        }

        boolean[] minRTsUsed = new boolean[] { false, false, false };
        if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus()) || BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus())) {
            minRTsUsed = ratedTransactionService.isMinRTsUsed();
        }
        boolean includesFirstRun = false;
        if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {

            int totalEntityCount = 0;

            // Use billable amount calculation one billable entity at a time when minimum billable amount rule is used or billable entities are provided as parameter of billing run
            // (billingCycle=null)
            // NOTE: invoice by order is also included here as there is no FK between Order and RT
            if (billingCycle == null || billingCycle.getType() == BillingEntityTypeEnum.ORDER || minRTsUsed[0] || minRTsUsed[1] || minRTsUsed[2]) {
                List<? extends IBillableEntity> entities = getEntitiesToInvoice(billingRun);

                totalEntityCount = entities != null ? entities.size() : 0;
                log.info(
                    "Will create min RTs and update billable amount totals for Billing run {} for {} entities of type {}. Minimum invoicing amount is used for serviceInstance {}, subscription {}, billingAccount {}",
                    billingRun.getId(), totalEntityCount, type, minRTsUsed[0], minRTsUsed[1], minRTsUsed[2]);
                if ((entities != null) && (entities.size() > 0)) {
                    SubListCreator subListCreator = new SubListCreator(entities, (int) nbRuns);
                    List<Future<List<IBillableEntity>>> asyncReturns = new ArrayList<Future<List<IBillableEntity>>>();
                    MeveoUser lastCurrentUser = currentUser.unProxy();
                    while (subListCreator.isHasNext()) {
                        Future<List<IBillableEntity>> billableEntitiesAsynReturn = invoicingAsync.calculateBillableAmountsAsync(subListCreator.getNextWorkSet(), billingRun,
                            jobInstanceId, minRTsUsed[0], minRTsUsed[1], minRTsUsed[2], lastCurrentUser);
                        asyncReturns.add(billableEntitiesAsynReturn);
                        try {
                            Thread.sleep(waitingMillis);
                        } catch (InterruptedException e) {
                            log.error("", e);
                        }
                    }

                    for (Future<List<IBillableEntity>> futureItsNow : asyncReturns) {
                        billableEntities.addAll(futureItsNow.get());
                    }
                }

                // A simplified form of calculating of total amounts when no need to worry about minimum amounts
            } else {
                List<AmountsToInvoice> billableAmountSummary = getAmountsToInvoice(billingRun);

                totalEntityCount = billableAmountSummary != null ? billableAmountSummary.size() : 0;

                log.info("Will create min RTs and update billable amount totals for Billing run {} for {} entities of type {}. Minimum invoicing amount is skipped.",
                    billingRun.getId(), totalEntityCount, type);

                if ((billableAmountSummary != null) && (billableAmountSummary.size() > 0)) {
                    SubListCreator<AmountsToInvoice> subListCreator = new SubListCreator(billableAmountSummary, (int) nbRuns);
                    List<Future<List<IBillableEntity>>> asyncReturns = new ArrayList<Future<List<IBillableEntity>>>();
                    MeveoUser lastCurrentUser = currentUser.unProxy();
                    while (subListCreator.isHasNext()) {
                        Future<List<IBillableEntity>> billableEntitiesAsynReturn = invoicingAsync.calculateBillableAmountsAsync(subListCreator.getNextWorkSet(), billingRun,
                            jobInstanceId, lastCurrentUser);
                        asyncReturns.add(billableEntitiesAsynReturn);
                        try {
                            Thread.sleep(waitingMillis);
                        } catch (InterruptedException e) {
                            log.error("", e);
                        }
                    }

                    for (Future<List<IBillableEntity>> futureItsNow : asyncReturns) {
                        billableEntities.addAll(futureItsNow.get());
                    }
                }

            }
            includesFirstRun = true;

            log.info("Will update BR amount totals for Billing run {}. Will invoice {} out of {} entities of type {}", billingRun.getId(),
                (billableEntities != null ? billableEntities.size() : 0), totalEntityCount, type);
            billingRunExtensionService.updateBRAmounts(billingRun.getId(), billableEntities);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), totalEntityCount, billableEntities.size(), BillingRunStatusEnum.PREINVOICED, new Date());
        }

        boolean proceedToPostInvoicing = BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus()) || (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())
                && ((billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC)
                        || appProvider.isAutomaticInvoicing()));

        if (proceedToPostInvoicing) {

            if (!includesFirstRun) {
                billableEntities = (List<IBillableEntity>) getEntitiesByBillingRun(billingRun);
            }

            boolean instantiateMinRts = !includesFirstRun && (minRTsUsed[0] || minRTsUsed[1] || minRTsUsed[2]);

            log.info("Will create invoices for Billing run {} for {} entities of type {}. Min RTs will {} be created. {}", billingRun.getId(),
                (billableEntities != null ? billableEntities.size() : 0), type, instantiateMinRts ? "" : "NOT", !instantiateMinRts ? ""
                        : "Minimum invoicing amount is used for serviceInstance " + minRTsUsed[0] + ", subscription " + minRTsUsed[1] + ", billingAccount " + minRTsUsed[2]);

            createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis, jobInstanceId, billableEntities, !includesFirstRun && minRTsUsed[0], !includesFirstRun && minRTsUsed[1],
                !includesFirstRun && minRTsUsed[2]);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.POSTINVOICED, null);
            if (billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC) {
                billingRun = billingRunExtensionService.findById(billingRun.getId());
            }
        }

        if (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus()) && billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC) {
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.POSTVALIDATED, null);
            billingRun = billingRunExtensionService.findById(billingRun.getId());
        }

        if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {

            log.info("Will assign invoice numbers to invoices of Billing run {} of type {}", billingRun.getId(), type);
            invoiceService.nullifyInvoiceFileNames(billingRun); // #3600
            assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, nbRuns, waitingMillis, jobInstanceId, result);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
        }
    }

    /**
     * Get amounts to invoice grouped by a billable entity a configured in billing run
     * 
     * @param billingRun Billing run
     * @return A list of Object array consisting billable entity id and amounts
     */
    private List<AmountsToInvoice> getAmountsToInvoice(BillingRun billingRun) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();

        if ((startDate != null) && (endDate == null)) {
            endDate = new Date();
        }

        String sqlName = billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION ? "RatedTransaction.sumTotalInvoiceableBySubscriptionInBatch"
                : startDate == null ? "RatedTransaction.sumTotalInvoiceableByBAInBatch" : "RatedTransaction.sumTotalInvoiceableByBAInBatchLimitByNextInvoiceDate";

        TypedQuery<AmountsToInvoice> query = getEntityManager().createNamedQuery(sqlName, AmountsToInvoice.class).setParameter("firstTransactionDate", new Date(0))
            .setParameter("lastTransactionDate", billingRun.getLastTransactionDate()).setParameter("billingCycle", billingCycle);

        if (billingCycle.getType() == BillingEntityTypeEnum.BILLINGACCOUNT && startDate != null) {

            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(startDate);
            calFrom.set(Calendar.HOUR_OF_DAY, 0);
            calFrom.set(Calendar.MINUTE, 0);
            calFrom.set(Calendar.SECOND, 0);
            calFrom.set(Calendar.MILLISECOND, 0);

            startDate = calFrom.getTime();

            Calendar calTo = Calendar.getInstance();
            calTo.setTime(endDate);
            calTo.set(Calendar.HOUR_OF_DAY, 0);
            calTo.set(Calendar.MINUTE, 0);
            calTo.set(Calendar.SECOND, 0);
            calTo.set(Calendar.MILLISECOND, 0);

            endDate = calTo.getTime();

            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    /**
     * Force validate.
     *
     * @param billingRunId the billing run id
     * @throws BusinessException the business exception
     */
    public void forceValidate(Long billingRunId) throws BusinessException {
        BillingRun billingRun = findById(billingRunId);
        if (billingRun == null) {
            throw new BusinessException("Cant find BillingRun with id:" + billingRunId);
        }
        detach(billingRun);
        log.debug("forceValidate, billingRun status={}", billingRun.getStatus());
        switch (billingRun.getStatus()) {

        case POSTINVOICED:
        case POSTVALIDATED:
            assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, 1, 0, null, null);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
            break;

        case PREINVOICED:
        case PREVALIDATED:
            createAgregatesAndInvoice(billingRun, 1, 0, null);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), 1, 0, BillingRunStatusEnum.POSTINVOICED, null);
            break;

        case VALIDATED:
        case CANCELED:
        case NEW:
        default:
            throw new BusinessException("BillingRun with status " + billingRun.getStatus() + " cannot be validated");
        }
    }

    /**
     * Launch invoicing rejected BA.
     *
     * @param br the br
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    public boolean launchInvoicingRejectedBA(BillingRun br) throws BusinessException {
        boolean result = false;
        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        BillingCycle billingCycle = br.getBillingCycle();
        if ((billingCycle != null) && (billingCycle.getInvoiceDateProductionDelay() != null)) {
            billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), billingCycle.getInvoiceDateProductionDelay()));
        } else {
            billingRun.setInvoiceDate(br.getProcessDate());
        }
        if ((billingCycle != null) && (billingCycle.getTransactionDateDelay() != null)) {
            billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), billingCycle.getTransactionDateDelay()));
        } else {
            billingRun.setLastTransactionDate(billingRun.getProcessDate());
        }
        billingRun.setProcessType(br.getProcessType());
        String selectedBillingAccounts = "";
        String sep = "";
        for (RejectedBillingAccount ba : br.getRejectedBillingAccounts()) {
            selectedBillingAccounts = selectedBillingAccounts + sep + ba.getId();
            sep = ",";
            if (!result && ratedTransactionService.isBillingAccountBillable(ba.getBillingAccount(), null, billingRun.getLastTransactionDate())) {
                result = true;
                break;
            }
        }
        if (result) {
            log.debug("selectedBillingAccounts=" + selectedBillingAccounts);
            billingRun.setSelectedBillingAccounts(selectedBillingAccounts);
            create(billingRun);
        }
        return result;
    }

    /**
     * Re-rate transactions that were invoiced by a billing run, mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any
     * invoices and invoice aggregates that were created during invoicing process created during invoicing process
     * 
     * @param billingRun Billing run to re-rate
     * @return Updated billing run
     */
    public BillingRun rerateBillingRun(BillingRun billingRun) {

        billingRun = refreshOrRetrieve(billingRun);

        if (billingRun.getStatus() == BillingRunStatusEnum.POSTINVOICED || billingRun.getStatus() == BillingRunStatusEnum.POSTVALIDATED) {
            walletOperationService.markToRerateByBR(billingRun);
            cleanBillingRun(billingRun);
        } else {
            ratedTransactionService.deleteMinRTs(billingRun);
        }

        billingRun.setStatus(BillingRunStatusEnum.CANCELED);
        billingRun = update(billingRun);

        return billingRun;
    }

    /**
     * Mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any invoices and invoice aggregates that were created during
     * invoicing process
     * 
     * @param billingRun Billing run to re-rate
     * @return Updated billing run
     */
    public BillingRun cancelBillingRun(BillingRun billingRun) {

        billingRun = refreshOrRetrieve(billingRun);

        if (billingRun.getStatus() == BillingRunStatusEnum.POSTINVOICED || billingRun.getStatus() == BillingRunStatusEnum.POSTVALIDATED
                || billingRun.getStatus() == BillingRunStatusEnum.CANCELLING) {
            cleanBillingRun(billingRun);
        } else {
            ratedTransactionService.deleteMinRTs(billingRun);
        }

        billingRun.setStatus(BillingRunStatusEnum.CANCELED);
        billingRun = update(billingRun);

        return billingRun;
    }
}