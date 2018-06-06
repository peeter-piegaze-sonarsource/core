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
import org.meveo.util.ApplicationProvider;

/**
 * @author anasseh
 * @author Phung tien lan
 * @lastModifiedVersion 5.1
 * 
 */
@Stateless
public class InvoiceTypeService extends BusinessService<InvoiceType> {

    @EJB
    private ServiceSingleton serviceSingleton;

    @Inject
    OCCTemplateService oCCTemplateService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ProviderService providerService;

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

    public Long getMaxCurrentInvoiceNumber(String invoiceTypeCode) throws BusinessException {
        Long max = getEntityManager().createNamedQuery("InvoiceType.currentInvoiceNb", Long.class)

            .setParameter("invoiceTypeCode", invoiceTypeCode).getSingleResult();

        return max == null ? 0 : max;

    }

    public InvoiceType getDefaultAdjustement() throws BusinessException {
        return getDefaultType(getAdjustementCode());
    }

    public InvoiceType getDefaultCommertial() throws BusinessException {
        return getDefaultType(getCommercialCode());
    }

    public InvoiceType getDefaultQuote() throws BusinessException {
        return getDefaultType(getQuoteCode());
    }

    public String getCommercialCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.commercial.code", "COM");
    }

    public String getAdjustementCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.adjustement.code", "ADJ");
    }

    public String getQuoteCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.quote.code", "QUOTE");
    }

    /**
     * Get a custom field code to track invoice numbering sequence for a given invoice type
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
     * @return
     * @throws BusinessException
     */
    public Long getCurrentGlobalInvoiceBb() throws BusinessException {
        appProvider = providerService.findById(appProvider.getId());
        Long currentInvoiceNb = appProvider.getInvoiceConfiguration().getCurrentInvoiceNb();
        if (currentInvoiceNb == null) {
            throw new BusinessException("Cant get global CurrentGlobalInvoiceNb ");
        }
        return currentInvoiceNb;
    }

    /**
     * @param l
     * @throws BusinessException
     */
    public void setCurrentGlobalInvoiceBb(Long l) throws BusinessException {
        try {

            appProvider = providerService.findById(appProvider.getId());

            appProvider.getInvoiceConfiguration().setCurrentInvoiceNb(l);

        } catch (Exception e) {
            throw new BusinessException("Cant update global InvoiceTypeSequence : " + e.getMessage());
        }
    }
}