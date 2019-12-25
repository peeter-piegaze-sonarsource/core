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

package org.meveo.admin.job;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.AutomatedRefund;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.DDRequestItemService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentHistoryService;
import org.slf4j.Logger;

/**
 * The Class UnitSepaDirectDebitJobBean.
 *
 * @author anasseh
 */

@Stateless
public class UnitSepaDirectDebitJobBean {

	/** The param bean factory. */
	@Inject
	protected ParamBeanFactory paramBeanFactory;

	/** The payment history service. */
	@Inject
	private PaymentHistoryService paymentHistoryService;

	/** The o CC template service. */
	@Inject
	private OCCTemplateService oCCTemplateService;

	/** The account operation service. */
	@Inject
	private AccountOperationService accountOperationService;

	/** The matching code service. */
	@Inject
	private MatchingCodeService matchingCodeService;

	/** The d D request item service. */
	@Inject
	private DDRequestItemService dDRequestItemService;

	/** The log. */
	@Inject
	private Logger log;

	/** The rejecteded edr producer. */
	@Inject
	@Rejected
	private Event<Serializable> rejectededEdrProducer;

	/**
	 * Execute processing one ddRequestItem.
	 *
	 * @param ddrequestItem the ddrequest item
	 * @throws BusinessException                the business exception
	 * @throws NoAllOperationUnmatchedException the no all operation unmatched
	 *                                          exception
	 * @throws UnbalanceAmountException         the unbalance amount exception
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, DDRequestItem ddrequestItem) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		ddrequestItem = dDRequestItemService.refreshOrRetrieve(ddrequestItem);
		DDRequestLOT ddRequestLOT = ddrequestItem.getDdRequestLOT();
		log.debug("processing DD requestItem id  : " + ddrequestItem.getId());
		AccountOperation automatedPayment = null;
		PaymentErrorTypeEnum paymentErrorTypeEnum = null;
		PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.ACCEPTED;
		String errorMsg = null;
		if (!ddrequestItem.hasError()) {
			if (BigDecimal.ZERO.compareTo(ddrequestItem.getAmount()) == 0) {
				log.info("invoice: {}  balanceDue:{}  no DIRECTDEBIT transaction", ddrequestItem.getReference(), BigDecimal.ZERO);
			} else {
				automatedPayment = createPaymentOrRefund(ddrequestItem, PaymentMethodEnum.DIRECTDEBIT, ddrequestItem.getAmount(),
						ddrequestItem.getAccountOperations().get(0).getCustomerAccount(), "ddItem" + ddrequestItem.getId(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(),
						DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()), ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(),
						ddrequestItem.getAccountOperations(), true, MatchingTypeEnum.A_DERICT_DEBIT);
				if (ddrequestItem.getDdRequestLOT().getPaymentOrRefundEnum().getOperationCategoryToProcess() == OperationCategoryEnum.CREDIT) {
					ddrequestItem.setAutomatedRefund((AutomatedRefund) automatedPayment);
				} else {
					ddrequestItem.setAutomatedPayment((AutomatedPayment) automatedPayment);

				}
			}
			if (result != null) {
				result.registerSucces();
			}
		} else {
			paymentErrorTypeEnum = PaymentErrorTypeEnum.ERROR;
			paymentStatusEnum = PaymentStatusEnum.ERROR;
			errorMsg = ddrequestItem.getErrorMsg();
			if (result != null) {
				result.registerError(errorMsg);
			}
		}

		paymentHistoryService.addHistory(ddrequestItem.getAccountOperations().get(0).getCustomerAccount(),
				(automatedPayment instanceof AutomatedPayment ? (Payment) automatedPayment : null),
				(automatedPayment instanceof Refund ? (Refund) automatedPayment : null), (ddrequestItem.getAmount().multiply(new BigDecimal(100))).longValue(),
				paymentStatusEnum, errorMsg, errorMsg, paymentErrorTypeEnum, ddrequestItem.getDdRequestLOT().getPaymentOrRefundEnum().getOperationCategoryToProcess(),
				ddRequestLOT.getDdRequestBuilder().getCode(), ddrequestItem.getAccountOperations().get(0).getCustomerAccount().getPreferredPaymentMethod());

	}

	/**
	 * Creates the payment/refund.
	 *
	 * @param                    <T> the generic type
	 * @param ddRequestItem      the dd request item
	 * @param paymentMethodEnum  the payment method enum
	 * @param amount             the amount
	 * @param customerAccount    the customer account
	 * @param reference          the reference
	 * @param bankLot            the bank lot
	 * @param depositDate        the deposit date
	 * @param bankCollectionDate the bank collection date
	 * @param dueDate            the due date
	 * @param transactionDate    the transaction date
	 * @param occForMatching     the occ for matching
	 * @param isToMatching       the is to matching
	 * @param matchingTypeEnum   the matching type enum
	 * @return the automated payment
	 * @throws BusinessException                the business exception
	 * @throws NoAllOperationUnmatchedException the no all operation unmatched
	 *                                          exception
	 * @throws UnbalanceAmountException         the unbalance amount exception
	 */
	@SuppressWarnings("unchecked")
	public <T extends AccountOperation> T createPaymentOrRefund(DDRequestItem ddRequestItem, PaymentMethodEnum paymentMethodEnum, BigDecimal amount,
			CustomerAccount customerAccount, String reference, String bankLot, Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate,
			List<AccountOperation> occForMatching, boolean isToMatching, MatchingTypeEnum matchingTypeEnum)
			throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		log.info("create payment for amount:" + amount + " paymentMethodEnum:" + paymentMethodEnum + " isToMatching:" + isToMatching + "  customerAccount:"
				+ customerAccount.getCode() + "...");

		ParamBean paramBean = paramBeanFactory.getInstance();
		String occTemplateCode = null;
		T automatedPayment = null;
		if (ddRequestItem.getDdRequestLOT().getPaymentOrRefundEnum().getOperationCategoryToProcess() == OperationCategoryEnum.CREDIT) {
			occTemplateCode = paramBean.getProperty("occ.refund.dd", "REF_DDT");
			automatedPayment = (T) new AutomatedRefund();
		} else {
			occTemplateCode = paramBean.getProperty("occ.payment.dd", "PAY_DDT");
			automatedPayment = (T) new AutomatedPayment();
		}

		OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
		if (occTemplate == null) {
			throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
		}

		automatedPayment.setPaymentMethod(paymentMethodEnum);
		automatedPayment.setAmount(amount);
		automatedPayment.setUnMatchingAmount(amount);
		automatedPayment.setMatchingAmount(BigDecimal.ZERO);
		automatedPayment.setAccountingCode(occTemplate.getAccountingCode());
		automatedPayment.setCode(occTemplate.getCode());
		automatedPayment.setDescription(occTemplate.getDescription());
		automatedPayment.setTransactionCategory(occTemplate.getOccCategory());
		automatedPayment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
		automatedPayment.setCustomerAccount(customerAccount);
		automatedPayment.setReference(reference);
		automatedPayment.setBankLot(bankLot);
		automatedPayment.setDepositDate(depositDate);
		automatedPayment.setBankCollectionDate(bankCollectionDate);
		automatedPayment.setDueDate(dueDate);
		automatedPayment.setTransactionDate(transactionDate);
		automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
		automatedPayment.setUnMatchingAmount(amount);
		automatedPayment.setMatchingAmount(BigDecimal.ZERO);
		automatedPayment.setDdRequestItem(ddRequestItem);
		automatedPayment.setSeller(ddRequestItem.getDdRequestLOT().getSeller());

		accountOperationService.create(automatedPayment);
		if (isToMatching) {
			List<Long> aoIds = new ArrayList<Long>();
			for (AccountOperation ao : occForMatching) {
				aoIds.add(ao.getId());
			}
			aoIds.add(automatedPayment.getId());
			matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIds, null, MatchingTypeEnum.A);
			log.info("matching created  for 1 automated Payment/Refund ");
		} else {
			log.info("no matching created ");
		}
		log.info("automated Payment/Refund created for amount:" + automatedPayment.getAmount());
		return automatedPayment;
	}
}