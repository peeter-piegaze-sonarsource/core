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
package org.meveo.admin.async;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.UnitSepaDirectDebitJobBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DDRequestItemService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.script.payment.AccountOperationFilterScript;

import static org.meveo.service.script.payment.AccountOperationFilterScript.LIST_AO_TO_PAY;

/**
 * The Class SepaDirectDebitAsync.
 *
 * @author anasseh
 */

@Stateless
public class SepaDirectDebitAsync {

    /**
     * The param bean factory.
     */
    @Inject
    protected ParamBeanFactory paramBeanFactory;

    /**
     * The unit SSD job bean.
     */
    @Inject
    private UnitSepaDirectDebitJobBean unitSSDJobBean;

    /**
     * The account operation service.
     */
    @Inject
    private AccountOperationService accountOperationService;

    /**
     * The dd request item service.
     */
    @Inject
    private DDRequestItemService ddRequestItemService;

    /**
     * The job execution service.
     */
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private SellerService sellerService;

    /**
     * Create payments for all items from the ddRequestLot. One Item at a time in a
     * separate transaction.
     *
     * @param ddRequestItems the dd request items
     * @param result         Job execution result
     * @return Future String
     * @throws BusinessException BusinessException
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForgetPaymentCreation(List<DDRequestItem> ddRequestItems, JobExecutionResultImpl result) throws BusinessException {
        for (DDRequestItem ddRequestItem : ddRequestItems) {

            if (result != null && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            try {
                unitSSDJobBean.execute(result, ddRequestItem);
            } catch (Exception e) {
                if (result != null) {
                    result.registerError(e.getMessage());
                }
            }
        }
        return new AsyncResult<String>("OK");
    }

    /**
     * Launch and forget DD request lot creation.
     *
     * @param ddRequestBuilder the dd request builder
     * @param listAoToPayIds   the list ao to pay
     * @param appProvider      the app provider
     * @return the future
     * @throws BusinessException the business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<DDRequestLOT> launchAndForgetDDRequestItemsCreation(DDRequestBuilder ddRequestBuilder, List<Long> listAoToPayIds, Provider appProvider,
            AccountOperationFilterScript filterScript, DDRequestLotOp ddRequestLotOp) throws BusinessException {
        DDRequestLOT ddRequestLOT = createDDReqItemsWithTransaction(ddRequestBuilder, listAoToPayIds, appProvider, filterScript, ddRequestLotOp);
        return new AsyncResult<>(ddRequestLOT);
    }

    public DDRequestLOT createDDReqItemsWithTransaction(DDRequestBuilder ddRequestBuilder, List<Long> listAoToPayIds, Provider appProvider,
            AccountOperationFilterScript filterScript, DDRequestLotOp ddRequestLotOp) {
        DDRequestLOT ddRequestLOT = new DDRequestLOT();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int nbItemsKo = 0;
        int nbItemsOk = 0;
        StringBuilder allErrors = new StringBuilder();
        List<DDRequestItem> ddrItems = new ArrayList<>();
        List<AccountOperation> listAoToPay = new ArrayList<>();
        for (Long aoId : listAoToPayIds) {
            AccountOperation ao = accountOperationService.findById(aoId);
            ao.setCustomerAccount(PersistenceUtils.initializeAndUnproxy(ao.getCustomerAccount()));
            listAoToPay.add(ao);
        }
        listAoToPay = this.filterAoToPayOrRefund(listAoToPay, ddRequestLotOp, filterScript);

        if (PaymentLevelEnum.AO == ddRequestBuilder.getPaymentLevel()) {
            for (AccountOperation ao : listAoToPay) {
                String errorMsg = getMissingField(ao, ddRequestLOT, appProvider);
                Name caName = ao.getCustomerAccount().getName();
                String caFullName = this.getCaFullName(caName);
                ddrItems.add(ddRequestItemService.createDDRequestItem(ao.getUnMatchingAmount(), ddRequestLOT, caFullName, errorMsg, Collections.singletonList(ao)));
                if (errorMsg != null) {
                    nbItemsKo++;
                    allErrors.append(errorMsg).append(" ; ");
                } else {
                    nbItemsOk++;
                    totalAmount = totalAmount.add(ao.getUnMatchingAmount());
                }
            }
        }
        if (PaymentLevelEnum.CA == ddRequestBuilder.getPaymentLevel()) {
            Map<CustomerAccount, List<AccountOperation>> aosByCA = listAoToPay.parallelStream()
                    .collect(Collectors.groupingBy(AccountOperation::getCustomerAccount, Collectors.toList()));

            for (Map.Entry<CustomerAccount, List<AccountOperation>> entry : aosByCA.entrySet()) {
                BigDecimal amountToPayByItem = BigDecimal.ZERO;
                StringBuilder allErrorsByItem = new StringBuilder();
                CustomerAccount ca = entry.getKey();
                String caFullName = this.getCaFullName(ca.getName());
                for (AccountOperation ao : entry.getValue()) {
                    String errorMsg = getMissingField(ao, ddRequestLOT, appProvider);
                    if (errorMsg != null) {
                        allErrorsByItem.append(errorMsg).append(" ; ");
                    } else {
                        amountToPayByItem = amountToPayByItem.add(ao.getUnMatchingAmount());
                    }
                }

                ddrItems.add(ddRequestItemService.createDDRequestItem(amountToPayByItem, ddRequestLOT, caFullName, allErrorsByItem.toString(), entry.getValue()));

                if (StringUtils.isBlank(allErrorsByItem.toString())) {
                    nbItemsOk++;
                    totalAmount = totalAmount.add(amountToPayByItem);
                } else {
                    nbItemsKo++;
                    allErrors.append(allErrorsByItem).append(" ; ");
                }
            }
        }
        ddRequestLOT.setDdrequestItems(ddrItems);
        ddRequestLOT.setNbItemsKo(nbItemsKo);
        ddRequestLOT.setNbItemsOk(nbItemsOk);
        ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors.toString(), 255, true));
        ddRequestLOT.setTotalAmount(totalAmount);
        return ddRequestLOT;
    }

    /**
     * Gets the ca full name.
     *
     * @param caName the ca name
     * @return the ca full name
     */
    private String getCaFullName(Name caName) {
        return caName != null ? caName.getFullName() : "";
    }

    /**
     * Gets the missing field.
     *
     * @param accountOperation the account operation
     * @param ddRequestLOT     the dd request LOT
     * @param appProvider      the app provider
     * @return the missing field
     * @throws BusinessException the business exception
     */
    public String getMissingField(AccountOperation accountOperation, DDRequestLOT ddRequestLOT, Provider appProvider) throws BusinessException {
        String prefix = "AO.id:" + accountOperation.getId() + " : ";
        CustomerAccount ca = accountOperation.getCustomerAccount();
        if (ca == null) {
            return prefix + "recordedInvoice.ca";
        }
        if (ca.getName() == null) {
            return prefix + "ca.name";
        }
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification() == null) {
                return prefix + "paymentMethod.mandateIdentification";
            }
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateDate() == null) {
                return prefix + "paymentMethod.mandateDate";
            }
        } else {
            return prefix + "DDPaymentMethod";
        }

        if (accountOperation.getUnMatchingAmount() == null) {
            return prefix + "invoice.amount";
        }
        if (StringUtils.isBlank(appProvider.getDescription())) {
            return prefix + "provider.description";
        }
        BankCoordinates bankCoordinates = null;
        if (ddRequestLOT.getSeller() != null) {

            PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(ddRequestLOT.getSeller(), PaymentMethodEnum.DIRECTDEBIT);
            if (paymentGateway == null) {
                throw new BusinessException("Cant find payment gateway for seller : " + ddRequestLOT.getSeller());
            }
            bankCoordinates = paymentGateway.getBankCoordinates();
        } else {
            bankCoordinates = appProvider.getBankCoordinates();
        }

        if (bankCoordinates == null) {
            return prefix + "provider or seller bankCoordinates";
        }
        if (bankCoordinates.getIban() == null) {
            return prefix + "bankCoordinates.iban";
        }
        if (bankCoordinates.getBic() == null) {
            return prefix + "bankCoordinates.bic";
        }
        if (bankCoordinates.getIcs() == null) {
            return prefix + "bankCoordinates.ics";
        }
        if (accountOperation.getReference() == null) {
            return prefix + "accountOperation.reference";
        }
        if (ca.getDescription() == null) {
            return prefix + "ca.description";
        }
        return null;
    }

    /**
     * Filter ao to pay or refund, based on a given script, which is set through a job CF.
     *
     * @param listAoToPay    the list ao to pay
     * @param ddRequestLotOp the dd request lot op
     * @return the accountOperation list to process
     */
    private List<AccountOperation> filterAoToPayOrRefund(List<AccountOperation> listAoToPay, DDRequestLotOp ddRequestLotOp, AccountOperationFilterScript aoFilterScript) {
        if (aoFilterScript != null) {
            Map<String, Object> methodContext = new HashMap<>();
            methodContext.put(LIST_AO_TO_PAY, listAoToPay);
            listAoToPay = aoFilterScript.filterAoToPay(methodContext);
        }
        Seller ddReqOpSeller = sellerService.refreshOrRetrieve(ddRequestLotOp.getSeller());
        if (CollectionUtils.isNotEmpty(listAoToPay)) {
            listAoToPay = listAoToPay.parallelStream().filter(getAccountOperationPredicate(ddRequestLotOp, ddReqOpSeller)).collect(Collectors.toList());
        }
        return listAoToPay;
    }

    private Predicate<AccountOperation> getAccountOperationPredicate(DDRequestLotOp ddRequestLotOp, Seller ddReqOpSeller) {
        return (ao) -> (ao.getPaymentMethod() == PaymentMethodEnum.DIRECTDEBIT && ao.getTransactionCategory() == ddRequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess()
                && (ao.getMatchingStatus() == MatchingStatusEnum.O || ao.getMatchingStatus() == MatchingStatusEnum.P) && (ddReqOpSeller == null || ao.getSeller().equals(ddReqOpSeller)));
    }

}
