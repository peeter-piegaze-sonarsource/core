/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.payment.RefundDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.payments.impl.RefundService;

/**
 *  @author Edward P. Legaspi
 *  @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class RefundApi extends BaseApi {

    @Inject
    private RefundService refundService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    /**
     * @param refundDto refund object which encapsulates the input data sent by client
     * @return the id of payment if created successful otherwise null
     * @throws NoAllOperationUnmatchedException no all operation unmatched exception
     * @throws UnbalanceAmountException unbalance amount exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public Long createRefund(RefundDto refundDto) throws NoAllOperationUnmatchedException, UnbalanceAmountException, BusinessException, MeveoApiException {
        log.info("create payment for amount:" + refundDto.getAmount() + " paymentMethodEnum:" + refundDto.getPaymentMethod() + " isToMatching:" + refundDto.isToMatching()
                + "  customerAccount:" + refundDto.getCustomerAccountCode() + "...");

        if (StringUtils.isBlank(refundDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (StringUtils.isBlank(refundDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(refundDto.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }
        if (StringUtils.isBlank(refundDto.getReference())) {
            missingParameters.add("reference");
        }
        if (StringUtils.isBlank(refundDto.getPaymentMethod())) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();
        CustomerAccount customerAccount = customerAccountService.findByCode(refundDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + refundDto.getCustomerAccountCode());
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(refundDto.getOccTemplateCode());
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + refundDto.getOccTemplateCode());
        }

        Refund refund = new Refund();
        refund.setPaymentMethod(refundDto.getPaymentMethod());
        refund.setAmount(refundDto.getAmount());
        refund.setUnMatchingAmount(refundDto.getAmount());
        refund.setMatchingAmount(BigDecimal.ZERO);
        refund.setAccountingCode(occTemplate.getAccountingCode());
        refund.setCode(occTemplate.getCode());
        refund.setDescription(StringUtils.isBlank(refundDto.getDescription()) ? occTemplate.getDescription() : refundDto.getDescription());
        refund.setTransactionCategory(occTemplate.getOccCategory());
        refund.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        refund.setCustomerAccount(customerAccount);
        refund.setReference(refundDto.getReference());
        refund.setDueDate(refundDto.getDueDate());
        refund.setTransactionDate(refundDto.getTransactionDate());
        refund.setMatchingStatus(MatchingStatusEnum.O);

        // populate customFields
        try {
            populateCustomFields(refundDto.getCustomFields(), refund, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        refundService.create(refund);

        if (refundDto.isToMatching()) {
            matchRefunds(refundDto, customerAccount, refund);
        } else {
            log.info("no matching created ");
        }
        log.debug("refund created for amount:" + refund.getAmount());

        return refund.getId();
    }

	private void matchRefunds(RefundDto refundDto, CustomerAccount customerAccount, Refund refund)
			throws BusinessApiException, BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		List<Long> listReferenceToMatch = new ArrayList<Long>();
		if (refundDto.getListAoIdsForMatching()!=null && !refundDto.getListAoIdsForMatching().isEmpty() ) {
			listReferenceToMatch.addAll(refundDto.getListAoIdsForMatching());
		} else if (refundDto.getListOCCReferenceforMatching() != null) {
		    for (String Reference: refundDto.getListOCCReferenceforMatching()) {
		        List<RecordedInvoice> accountOperationToMatch = recordedInvoiceService.getRecordedInvoice(Reference);
		        if (accountOperationToMatch == null || accountOperationToMatch.isEmpty()) {
		            throw new BusinessApiException("Cannot find account operation with reference:" + Reference );
		        } else if (accountOperationToMatch.size() > 1) {
		            throw new BusinessApiException("More than one account operation with reference:" + Reference +". Please use ListAoIdsForMatching instead of ListOCCReferenceforMatching");
		        }
		        listReferenceToMatch.add(accountOperationToMatch.get(0).getId());
		    }
		}
		listReferenceToMatch.add(refund.getId());
		matchingCodeService.matchOperations(null, customerAccount.getCode(), listReferenceToMatch, null, MatchingTypeEnum.A);
	}

    public List<RefundDto> getRefundList(String customerAccountCode) throws Exception {
        List<RefundDto> result = new ArrayList<>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        List<AccountOperation> ops = customerAccount.getAccountOperations();
        for (AccountOperation op : ops) {
            if (op instanceof Refund) {
                Refund refund = (Refund) op;
                RefundDto refundDto = new RefundDto();
                refundDto.setType(refund.getType());
                refundDto.setAmount(refund.getAmount());
                refundDto.setDueDate(refund.getDueDate());
                refundDto.setOccTemplateCode(refund.getCode());
                refundDto.setPaymentMethod(refund.getPaymentMethod());
                refundDto.setReference(refund.getReference());
                refundDto.setTransactionDate(refund.getTransactionDate());
                refundDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(op, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                result.add(refundDto);
            }
        }
        return result;
    }

    public PaymentResponseDto refundByCard(PayByCardDto cardPaymentRequestDto)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, MeveoApiException {

        if (StringUtils.isBlank(cardPaymentRequestDto.getCtsAmount())) {
            missingParameters.add("ctsAmount");
        }

        if (StringUtils.isBlank(cardPaymentRequestDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        boolean useCard = false;

        // case card payment
        if (!StringUtils.isBlank(cardPaymentRequestDto.getCardNumber())) {
            useCard = true;
            if (StringUtils.isBlank(cardPaymentRequestDto.getCvv())) {
                missingParameters.add("cvv");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getExpiryDate()) || cardPaymentRequestDto.getExpiryDate().length() != 4
                    || !org.apache.commons.lang3.StringUtils.isNumeric(cardPaymentRequestDto.getExpiryDate())) {

                missingParameters.add("expiryDate");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getOwnerName())) {
                missingParameters.add("ownerName");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getCardType())) {
                missingParameters.add("cardType");
            }
        }
        if (cardPaymentRequestDto.isToMatch()) {
            if (cardPaymentRequestDto.getAoToPay() == null || cardPaymentRequestDto.getAoToPay().isEmpty()) {
                missingParameters.add("aoToPay");
            }
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(cardPaymentRequestDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, cardPaymentRequestDto.getCustomerAccountCode());
        }

        PaymentMethodEnum preferedMethod = customerAccount.getPreferredPaymentMethodType();
        if (preferedMethod != null && PaymentMethodEnum.CARD != preferedMethod) {
            throw new BusinessApiException("Can not process payment as prefered payment method is " + preferedMethod);
        }

        PaymentResponseDto doPaymentResponseDto = null;
        if (useCard) {

            doPaymentResponseDto = paymentService.refundByCard(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getCardNumber(),
                cardPaymentRequestDto.getOwnerName(), cardPaymentRequestDto.getCvv(), cardPaymentRequestDto.getExpiryDate(), cardPaymentRequestDto.getCardType(),
                cardPaymentRequestDto.getAoToPay(), cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        } else {
            doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getAoToPay(),
                cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        }

        return doPaymentResponseDto;
    }

}