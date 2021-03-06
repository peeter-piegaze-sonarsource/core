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
package org.meveo.service.billing.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * The Class InvoiceTypeService.
 *
 * @author anasseh
 * @author Phung tien lan
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class InvoiceTypeService extends BusinessService<InvoiceType> {

    /** The service singleton. */
    @EJB
    private ServiceSingleton serviceSingleton;

    /** The o CC template service. */
    @Inject
    OCCTemplateService oCCTemplateService;

    @Inject
    private ProviderService providerService;

    /**
     * Gets the default type.
     *
     * @param invoiceTypeCode the invoice type code
     * @return the default type
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultType(String invoiceTypeCode) throws BusinessException {

        InvoiceType defaultInvoiceType = findByCode(invoiceTypeCode);
        if (defaultInvoiceType != null) {
            return defaultInvoiceType;
        }

        String occCode = "accountOperationsGenerationJob.occCode";
        String occCodeDefaultValue = "INV_STD";
        OperationCategoryEnum operationCategory = OperationCategoryEnum.DEBIT;
        if (getAdjustementCode().equals(invoiceTypeCode)) {
            occCode = "accountOperationsGenerationJob.occCodeAdjustement";
            occCodeDefaultValue = "INV_CRN";
            operationCategory = OperationCategoryEnum.CREDIT;
        }

        defaultInvoiceType = serviceSingleton.createInvoiceType(occCode, occCodeDefaultValue, invoiceTypeCode, operationCategory);

        return defaultInvoiceType;
    }

    /**
     * Gets the default adjustement.
     *
     * @return the default adjustement
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultAdjustement() throws BusinessException {
        return getDefaultType(getAdjustementCode());
    }

    /**
     * Gets the default commertial.
     *
     * @return the default commertial
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultCommertial() throws BusinessException {
        return getDefaultType(getCommercialCode());
    }

    /**
     * Gets the default quote.
     *
     * @return the default quote
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultQuote() throws BusinessException {
        return getDefaultType(getQuoteCode());
    }
    
    /**
     * Gets the default draft.
     *
     * @return the default draft
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultDraft() throws BusinessException {
        return getDefaultType(getDraftCode());
    }

    /**
     * Gets the default draft.
     *
     * @return the default draft
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultPrepaid() throws BusinessException {
        return getDefaultType(getPrepaidCode());
    }

    /**
     * Gets the commercial code.
     *
     * @return the commercial code
     */
    public String getCommercialCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.commercial.code", "COM");
    }

    /**
     * Gets the adjustement code.
     *
     * @return the adjustement code
     */
    public String getAdjustementCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.adjustement.code", "ADJ");
    }

    /**
     * Gets the quote code.
     *
     * @return the quote code
     */
    public String getQuoteCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.quote.code", "QUOTE");
    }
    
    /**
     * Gets the draft code.
     *
     * @return the draft code
     */
    public String getDraftCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.draft.code", "DRAFT");
    }

    /**
     * Gets the prepaid code.
     *
     * @return the prepaid code
     */
    public String getPrepaidCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.prepaid.code", "PREPAID");
    }

    /**
     * Get a custom field code to track invoice numbering sequence for a given invoice type.
     *
     * @param invoiceType Invoice type
     * @return A custom field code
     */
    public String getCustomFieldCode(InvoiceType invoiceType) {
        String cfName = "INVOICE_SEQUENCE_" + invoiceType.getCode().toUpperCase();
        if (getAdjustementCode().equals(invoiceType.getCode())) {
            cfName = "INVOICE_ADJUSTMENT_SEQUENCE";
        }
        if (getCommercialCode().equals(invoiceType.getCode())) {
            cfName = "INVOICE_SEQUENCE";
        }

        return cfName;
    }

	/**
	 * @return currentInvoiceNb
	 * @throws BusinessException business exception
	 */
	public Long getCurrentGlobalInvoiceBb() throws BusinessException {		
		Provider provider = providerService.findById(Provider.CURRENT_PROVIDER_ID, true);
		Long currentInvoiceNb = provider.getInvoiceConfiguration().getCurrentInvoiceNb();
		if (currentInvoiceNb == null) {
			currentInvoiceNb = 0L;
		}
		return currentInvoiceNb;
	}

	/**
	 * @param currentInvoiceNb
	 * @throws BusinessException business exception
	 */
	public void setCurrentGlobalInvoiceBb(Long currentInvoiceNb) throws BusinessException {
		try {			
			Provider provider = providerService.findById(Provider.CURRENT_PROVIDER_ID, true);
			provider.getInvoiceConfiguration().setCurrentInvoiceNb(currentInvoiceNb);

		} catch (Exception e) {
			throw new BusinessException("Cant update global InvoiceTypeSequence : " + e.getMessage());
		}
	}
}