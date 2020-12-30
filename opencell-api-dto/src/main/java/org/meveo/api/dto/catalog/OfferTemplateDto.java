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

package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferProductsDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;

/**
 * The Class OfferTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 */
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends ProductOfferingDto {

    /** The Constant serialVersionUID. */
    protected static final long serialVersionUID = 9156372453581362595L;

    /** The bom code. */
    protected String bomCode;

    /** The offer template category code. */
    @Deprecated
    protected String offerTemplateCategoryCode;

    /** The offer service templates. */
    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    protected List<OfferServiceTemplateDto> offerServiceTemplates;

    /** The offer product templates. */
    @Deprecated
    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    protected List<OfferProductTemplateDto> offerProductTemplates;
    
    /** The offer component. */
    @XmlElementWrapper(name = "offerProducts")
    @XmlElement(name = "offerProducts")
    protected List<OfferProductsDto> offerProducts=new ArrayList<OfferProductsDto>();

    /** The offer product templates. */
    @XmlElementWrapper(name = "allowedDiscountPlans")
    @XmlElement(name = "allowedDiscountPlans")
    protected List<DiscountPlanDto> allowedDiscountPlans;
    
    
    /** The offer product templates. */
    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attributes")
    protected List<AttributeDTO> attributes;
    

    /** The renewal rule. */
    protected SubscriptionRenewalDto renewalRule;

    /**
     * Expression to determine minimum amount value
     */
    protected String minimumAmountEl;

    /**
     * Expression to determine minimum amount value - for Spark
     */
    protected String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    protected String minimumLabelEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    protected String minimumLabelElSpark;

    /**
     * Corresponding to minimum invoice subcategory
     */
    protected String minimumInvoiceSubCategory;


    protected Boolean autoEndOfEngagement;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    protected String minimumChargeTemplate;
    
    /** The tags. */ 
    @XmlElementWrapper(name = "tagCodes")
    @XmlElement(name = "tagCodes")
    protected Set<String> tagCodes = new HashSet<String>();

    /**
     * Instantiates a new offer template dto.
     */
    public OfferTemplateDto() {

    }

    /**
     * Constructor.
     *
     * @param offerTemplate Offer template entity
     * @param customFieldsDto Custom fields DTO
     * @param asLink Convert to DTO with minimal information only - code and validity dates
     */
    public OfferTemplateDto(OfferTemplate offerTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
        super(offerTemplate, customFieldsDto, asLink);
        id = offerTemplate.getId();

        if (offerTemplate.getBusinessOfferModel() != null) {
            setBomCode(offerTemplate.getBusinessOfferModel().getCode());
        }
        if (!asLink) {
            setRenewalRule(new SubscriptionRenewalDto(offerTemplate.getSubscriptionRenewal()));
        }
    }

    @Override
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    @Override
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the bom code.
     *
     * @return the bom code
     */
    public String getBomCode() {
        return bomCode;
    }

    /**
     * Sets the bom code.
     *
     * @param bomCode the new bom code
     */
    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    /**
     * Gets the offer template category code.
     *
     * @return the offer template category code
     */
    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    /**
     * Sets the offer template category code.
     *
     * @param offerTemplateCategoryCode the new offer template category code
     */
    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    /**
     * Gets the offer service templates.
     *
     * @return the offer service templates
     */
    public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    /**
     * Sets the offer service templates.
     *
     * @param offerServiceTemplates the new offer service templates
     */
    public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    /**
     * Gets the offer product templates.
     *
     * @return the offer product templates
     */
    public List<OfferProductTemplateDto> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    /**
     * Sets the offer product templates.
     *
     * @param offerProductTemplates the new offer product templates
     */
    public void setOfferProductTemplates(List<OfferProductTemplateDto> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    /**
     * Gets the list of allowed Discount Plans.
     *
     * @return the Allowed Discount Plans
     */
    public List<DiscountPlanDto> getAllowedDiscountPlans() {
        return allowedDiscountPlans;
    }

    /**
     * Sets the list of allowed Discount Plans.
     *
     * @param allowedDiscountPlans the new list of allowed Discount Plans
     */
    public void setAllowedDiscountPlans(List<DiscountPlanDto> allowedDiscountPlans) {
        this.allowedDiscountPlans = allowedDiscountPlans;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    /**
     * Gets the renewal rule.
     *
     * @return the renewal rule
     */
    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    /**
     * Sets the renewal rule.
     *
     * @param renewalRule the new renewal rule
     */
    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
    }

    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", longDescription=" + longDescription + ", disabled=" + isDisabled() + ", bomCode="
                + bomCode + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", offerServiceTemplates=" + offerServiceTemplates + ", customFields=" + customFields
                + ", validFrom=" + validFrom + ", validTo=" + validTo + "]";
    }

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    /**
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    /**
     * Gets the Minimum InvoiceSubCategory used in the offer code
     * @return the Minimum InvoiceSubCategory used in the offer code
     */
    public String getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * Sets the Minimum InvoiceSubCategory used in the offer code
     *
     * @param minimumInvoiceSubCategory
     */
    public void setMinimumInvoiceSubCategory(String minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    public String getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    public void setMinimumChargeTemplate(String minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }



 
	/**
	 * @return the tagCodes
	 */
	public Set<String> getTagCodes() {
		return tagCodes;
	}

	/**
	 * @param tagCodes the tagCodes to set
	 */
	public void setTagCodes(Set<String> tagCodes) {
		this.tagCodes = tagCodes;
	}

	/**
	 * @return the offerProducts
	 */
	public List<OfferProductsDto> getOfferProducts() {
		return offerProducts;
	}

	/**
	 * @param offerProducts the offerProducts to set
	 */
	public void setOfferProducts(List<OfferProductsDto> offerProducts) {
		this.offerProducts = offerProducts;
	}

	/**
	 * @return the attributes
	 */
	public List<AttributeDTO> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<AttributeDTO> attributes) {
		this.attributes = attributes;
	}


	




 
    
	
	
	
    
}