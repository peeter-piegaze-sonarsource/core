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

package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OfferProductTemplateDto;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class OfferTemplateApi extends ProductOfferingApi<OfferTemplate, OfferTemplateDto> {

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private SubscriptionApi subscriptionApi;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private DiscountPlanService discountPlanService;
    
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate create(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }
        if (postData.getLifeCycleStatus() == null) {
            postData.setLifeCycleStatus(LifeCycleStatusEnum.IN_DESIGN);
        }
        handleMissingParameters();

        if (offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(),
                postData.getValidTo()) != null) {
            throw new EntityAlreadyExistsException(OfferTemplate.class,
                    postData.getCode() + " / " + postData.getValidFrom() + " / " + postData.getValidTo());
        }

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), null, true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException(
                "An offer, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
                        + ", already exists. Please change the validity dates of an existing offer first.");
        }

        OfferTemplate offerTemplate = populateFromDto(postData, null);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplateService.create(offerTemplate);

        return offerTemplate;
    }

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate update(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }
        handleMissingParametersAndValidate(postData);

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo());
        if (offerTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode() + " / " + DateUtils.formatDateWithPattern(postData.getValidFrom(), datePattern) + " / "
                    + DateUtils.formatDateWithPattern(postData.getValidTo(), datePattern));
        }

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getNewValidFrom(), postData.getNewValidTo(), offerTemplate.getId(),
            true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException(
                "An offer, valid on " + new DatePeriod(postData.getNewValidFrom(), postData.getNewValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
                        + ", already exists. Please change the validity dates of an existing offer first.");
        }

        offerTemplate = populateFromDto(postData, offerTemplate);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplate = offerTemplateService.update(offerTemplate);

        return offerTemplate;
    }

    private OfferTemplate populateFromDto(OfferTemplateDto postData, OfferTemplate offerTemplateToUpdate) throws MeveoApiException, BusinessException {

        OfferTemplate offerTemplate = offerTemplateToUpdate;

        if (offerTemplate == null) {
            offerTemplate = new OfferTemplate();
            if (postData.isDisabled() != null) {
                offerTemplate.setDisabled(postData.isDisabled());
            }
        }

        offerTemplate.setOfferChangeRestricted(postData.isOfferChangeRestricted());

        if(postData.getAllowedOfferChange() != null && !postData.getAllowedOfferChange().isEmpty()){
            List<OfferTemplate> allowedOffers = new ArrayList<>();
            for (String offerTemplateCode : postData.getAllowedOfferChange()){
                OfferTemplate allowedOffer = offerTemplateService.findByCode(offerTemplateCode);
                if(allowedOffer == null){
                    throw new EntityDoesNotExistsException(OfferTemplate.class, offerTemplateCode);
                }
                allowedOffers.add(allowedOffer);
            }
            offerTemplate.setAllowedOffersChange(allowedOffers);
        }
        
        Boolean autoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (autoEndOfEngagement != null) {
            offerTemplate.setAutoEndOfEngagement(autoEndOfEngagement);
        }

        BusinessOfferModel businessOffer = null;
        if (!StringUtils.isBlank(postData.getBomCode())) {
            businessOffer = businessOfferModelService.findByCode(postData.getBomCode());
            if (businessOffer == null) {
                throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
            }
        }

        if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
            OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode());
            if (offerTemplateCategory == null) {
                throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
            }
            offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
        }

        if (postData.getOfferTemplateCategories() != null) {
            offerTemplate.getOfferTemplateCategories().clear();
            for (OfferTemplateCategoryDto categoryDto : postData.getOfferTemplateCategories()) {
                OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryDto.getCode());
                if (offerTemplateCategory == null) {
                    throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryDto.getCode());
                }
                offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
            }
        }

        if (!StringUtils.isBlank(postData.getGlobalRatingScriptInstance())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getGlobalRatingScriptInstance());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getGlobalRatingScriptInstance());
            }
            offerTemplate.setGlobalRatingScriptInstance(scriptInstance);
        }

        if (postData.getSellers() != null) {
            offerTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                offerTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null) {
            offerTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                offerTemplate.addChannel(channel);
            }
        }

        if(postData.getCustomerCategories() != null) {
            offerTemplate.getCustomerCategories().clear();
            addCustomerCategories(postData.getCustomerCategories(), offerTemplate);
        }

        offerTemplate.setBusinessOfferModel(businessOffer);
        offerTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        offerTemplate.setDescription(postData.getDescription());
        offerTemplate.setName(postData.getName());
        offerTemplate.setLongDescription(postData.getLongDescription());
        if(postData.getNewValidFrom() != null && postData.getNewValidTo() != null) {
            offerTemplate.setValidity(new DatePeriod(postData.getNewValidFrom(), postData.getNewValidTo()));
        } else {
            offerTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        }
        offerTemplate.setMinimumAmountEl(postData.getMinimumAmountEl());
        offerTemplate.setMinimumLabelEl(postData.getMinimumLabelEl());
        offerTemplate.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
        offerTemplate.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());

        if (!StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
            } else {
                offerTemplate.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }
        if (postData.getLifeCycleStatus() != null) {
            offerTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
        }

        if (postData.getLanguageDescriptions() != null) {
            offerTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), offerTemplate.getDescriptionI18n()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            offerTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), offerTemplate.getLongDescriptionI18n()));
        }

        offerTemplate.setSubscriptionRenewal(subscriptionApi.subscriptionRenewalFromDto(offerTemplate.getSubscriptionRenewal(), postData.getRenewalRule(), false));
        processAllowedDiscountPlans(postData, offerTemplate);
        try {
        	String imagePath = postData.getImagePath();
			if(StringUtils.isBlank(imagePath) && StringUtils.isBlank(postData.getImageBase64())) {
        		deleteImage(offerTemplate);
        		offerTemplate.setImagePath(imagePath);
        	}else {
        		saveImage(offerTemplate, imagePath, postData.getImageBase64());
        	}
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        // check service templates
        processOfferServiceTemplates(postData, offerTemplate);

        // check offer product templates
        processOfferProductTemplates(postData, offerTemplate);

        return offerTemplate;
    }

    private void addCustomerCategories(List<CustomerCategoryDto> customerCategoryDtos, OfferTemplate offerTemplate) {
              for (CustomerCategoryDto categoryDto : customerCategoryDtos) {
                CustomerCategory customerCategory = customerCategoryService.findByCode(categoryDto.getCode());
                if (customerCategory == null) {
                    throw new EntityDoesNotExistsException(CustomerCategory.class, categoryDto.getCode());
                }
                offerTemplate.addCustomerCategory(customerCategory);
            }
    }

    private void processAllowedDiscountPlans(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        List<DiscountPlanDto> allowedDiscountPlans = postData.getAllowedDiscountPlans();
        if(allowedDiscountPlans != null && !allowedDiscountPlans.isEmpty()){
            offerTemplate.setAllowedDiscountPlans(allowedDiscountPlans
                    .stream()
                    .map(discountPlanDto -> discountPlanService.findByCode(discountPlanDto.getCode()))
                    .collect(Collectors.toList()));
        }
    }

    private void processOfferServiceTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
        List<OfferServiceTemplateDto> offerServiceTemplateDtos = postData.getOfferServiceTemplates();
        boolean hasOfferServiceTemplateDtos = offerServiceTemplateDtos != null && !offerServiceTemplateDtos.isEmpty();

        List<OfferServiceTemplate> existingServiceTemplates = offerTemplate.getOfferServiceTemplates();
        boolean hasExistingServiceTemplates = existingServiceTemplates != null && !existingServiceTemplates.isEmpty();

        if (hasOfferServiceTemplateDtos) {
            List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
            OfferServiceTemplate offerServiceTemplate = null;
            for (OfferServiceTemplateDto offerServiceTemplateDto : offerServiceTemplateDtos) {
                offerServiceTemplate = getOfferServiceTemplatesFromDto(offerServiceTemplateDto);
                offerServiceTemplate.setOfferTemplate(offerTemplate);
                newOfferServiceTemplates.add(offerServiceTemplate);
            }

            if (!hasExistingServiceTemplates) {
                offerTemplate.getOfferServiceTemplates().addAll(newOfferServiceTemplates);

            } else {

                // Keep only services that repeat
                existingServiceTemplates.retainAll(newOfferServiceTemplates);

                // Update existing services or add new ones
                for (OfferServiceTemplate ostNew : newOfferServiceTemplates) {

                    int index = existingServiceTemplates.indexOf(ostNew);
                    if (index >= 0) {
                        OfferServiceTemplate ostOld = existingServiceTemplates.get(index);
                        ostOld.update(ostNew);

                    } else {
                        existingServiceTemplates.add(ostNew);
                    }
                }
            }

        } else if (hasExistingServiceTemplates) {
            offerTemplate.getOfferServiceTemplates().removeAll(existingServiceTemplates);
        }
    }

    private void processOfferProductTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
        List<OfferProductTemplateDto> offerProductTemplateDtos = postData.getOfferProductTemplates();
        boolean hasOfferProductTemplateDtos = offerProductTemplateDtos != null && !offerProductTemplateDtos.isEmpty();
        List<OfferProductTemplate> existingProductTemplates = offerTemplate.getOfferProductTemplates();
        boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
        if (hasOfferProductTemplateDtos) {
            List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();
            OfferProductTemplate offerProductTemplate = null;
            for (OfferProductTemplateDto offerProductTemplateDto : offerProductTemplateDtos) {
                offerProductTemplate = getOfferProductTemplatesFromDto(offerProductTemplateDto);
                offerProductTemplate.setOfferTemplate(offerTemplate);
                newOfferProductTemplates.add(offerProductTemplate);
            }

            if (hasExistingProductTemplates) {
                List<OfferProductTemplate> offerProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
                offerProductTemplatesForRemoval.removeAll(newOfferProductTemplates);
                List<OfferProductTemplate> retainOfferProductTemplates = new ArrayList<>(newOfferProductTemplates);
                retainOfferProductTemplates.retainAll(existingProductTemplates);
                offerProductTemplatesForRemoval.addAll(retainOfferProductTemplates);
                newOfferProductTemplates.removeAll(new ArrayList<>(existingProductTemplates));
                offerTemplate.getOfferProductTemplates().removeAll(new ArrayList<>(offerProductTemplatesForRemoval));
                offerTemplate.getOfferProductTemplates().addAll(retainOfferProductTemplates);
            }

            offerTemplate.getOfferProductTemplates().addAll(newOfferProductTemplates);

        } else if (hasExistingProductTemplates) {
            offerTemplate.getOfferProductTemplates().removeAll(existingProductTemplates);
        }
    }

    private OfferServiceTemplate getOfferServiceTemplatesFromDto(OfferServiceTemplateDto offerServiceTemplateDto) throws MeveoApiException, BusinessException {

        ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
        ServiceTemplate serviceTemplate = null;
        if (serviceTemplateDto != null) {
            serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode());
            if (serviceTemplate == null) {
                throw new MeveoApiException(String.format("ServiceTemplatecode %s does not exist.", serviceTemplateDto.getCode()));
            }
        }

        OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
        Boolean mandatory = offerServiceTemplateDto.getMandatory();
        mandatory = mandatory == null ? false : mandatory;

        offerServiceTemplate.setServiceTemplate(serviceTemplate);
        offerServiceTemplate.setMandatory(mandatory);

        if (offerServiceTemplateDto.getIncompatibleServices() != null) {
            List<ServiceTemplate> incompatibleServices = new ArrayList<>();
            for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
                ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode());
                if (incompatibleService == null) {
                    throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
                }
                incompatibleServices.add(incompatibleService);
            }
            offerServiceTemplate.setIncompatibleServices(incompatibleServices);
        }

        return offerServiceTemplate;
    }

    private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto) throws MeveoApiException, BusinessException {

        ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
        ProductTemplate productTemplate = null;
        if (productTemplateDto != null) {
            productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), offerProductTemplateDto.getProductTemplate().getValidFrom(),
                offerProductTemplateDto.getProductTemplate().getValidTo());
            if (productTemplate == null) {
                throw new MeveoApiException(String.format("ProductTemplate %s / %s / %s does not exist.", productTemplateDto.getCode(),
                    offerProductTemplateDto.getProductTemplate().getValidFrom(), offerProductTemplateDto.getProductTemplate().getValidTo()));
            }
        }

        OfferProductTemplate offerProductTemplate = new OfferProductTemplate();
        Boolean mandatory = offerProductTemplateDto.getMandatory();
        mandatory = mandatory == null ? false : mandatory;

        offerProductTemplate.setProductTemplate(productTemplate);
        offerProductTemplate.setMandatory(mandatory);

        return offerProductTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiVersionedService#find(java.lang.String)
     */
    @Override
    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public OfferTemplateDto find(String code, Date validFrom, Date validTo) throws MeveoApiException {
        return find(code, validFrom, validTo, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, true, true, true, true, true);
    }

    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public OfferTemplateDto find(String code, Date validFrom, Date validTo, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate,
            boolean loadOfferProductTemplate, boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("offerTemplateCode");
            handleMissingParameters();
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (offerTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        return fromOfferTemplate(offerTemplate, inheritCF, loadOfferServiceTemplate, loadOfferProductTemplate, loadServiceChargeTemplate, loadProductChargeTemplate, loadAllowedDiscountPlan);
    }

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate createOrUpdate(OfferTemplateDto postData) throws MeveoApiException, BusinessException {
        return super.createOrUpdate(postData);
    }

    public OfferTemplateDto fromOfferTemplate(OfferTemplate offerTemplate) {
        return fromOfferTemplate(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, true, true, true, true, true);
    }

    public OfferTemplateDto fromOfferTemplate(OfferTemplate offerTemplate, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate,
            boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) {

        OfferTemplateDto dto = new OfferTemplateDto(offerTemplate, entityToDtoConverter.getCustomFieldsDTO(offerTemplate, inheritCF), false);
        dto.setMinimumAmountEl(offerTemplate.getMinimumAmountEl());
        dto.setMinimumLabelEl(offerTemplate.getMinimumLabelEl());
        dto.setMinimumAmountElSpark(offerTemplate.getMinimumAmountElSpark());
        dto.setMinimumLabelElSpark(offerTemplate.getMinimumLabelElSpark());
        
        if (loadOfferServiceTemplate && offerTemplate.getOfferServiceTemplates() != null && !offerTemplate.getOfferServiceTemplates().isEmpty()) {
            List<OfferServiceTemplateDto> offerTemplateServiceDtos = new ArrayList<>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerTemplateServiceDtos.add(new OfferServiceTemplateDto(st, entityToDtoConverter.getCustomFieldsDTO(st.getServiceTemplate(), inheritCF), loadServiceChargeTemplate));
            }
            dto.setOfferServiceTemplates(offerTemplateServiceDtos);
        }

        if(loadOfferProductTemplate) {
            List<OfferProductTemplate> childOfferProductTemplates = offerTemplate.getOfferProductTemplates();
            if (childOfferProductTemplates != null && !childOfferProductTemplates.isEmpty()) {
                List<OfferProductTemplateDto> offerProductTemplates = new ArrayList<>();
                OfferProductTemplateDto offerProductTemplateDto = null;
                ProductTemplate productTemplate = null;
                for (OfferProductTemplate offerProductTemplate : childOfferProductTemplates) {
                    productTemplate = offerProductTemplate.getProductTemplate();
                    offerProductTemplateDto = new OfferProductTemplateDto();
                    offerProductTemplateDto.setMandatory(offerProductTemplate.isMandatory());
                    if (productTemplate != null) {
                        offerProductTemplateDto.setProductTemplate(new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate, inheritCF), false, loadProductChargeTemplate));
                    }
                    offerProductTemplates.add(offerProductTemplateDto);
                }
                dto.setOfferProductTemplates(offerProductTemplates);
            }
        }

        if(loadAllowedDiscountPlan) {
            List<DiscountPlan> allowedDiscountPlans = offerTemplate.getAllowedDiscountPlans();
            if (allowedDiscountPlans != null && !allowedDiscountPlans.isEmpty()) {
                List<DiscountPlanDto> discountPlanDtos = new ArrayList<>();
                for (DiscountPlan discountPlan : allowedDiscountPlans) {
                    discountPlanDtos.add(new DiscountPlanDto(discountPlan, entityToDtoConverter.getCustomFieldsDTO(discountPlan)));
                }
                dto.setAllowedDiscountPlans(discountPlanDtos);
            }
        }

        return dto;
    }

    /**
     * List Offer templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return offers valid on a given date. If only validTo date is
     * provided, a search will return offers valid from today to a given date.
     * 
     * @param code Offer template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of offer templates
     * @throws InvalidParameterException invalid parametter exception.
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "offerTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListOfferTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering)
            throws InvalidParameterException {
        return list(code, validFrom, validTo, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "offerTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListOfferTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering,
            CustomFieldInheritanceEnum inheritCF) throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (!StringUtils.isBlank(code) || validFrom != null || validTo != null) {

            if (!StringUtils.isBlank(code)) {
                pagingAndFiltering.addFilter("code", code);
            }

            // If only validTo date is provided, a search will return products valid from today to a given date.
            if (validFrom == null && validTo != null) {
                validFrom = new Date();
            }

            // search by a single date
            if (validFrom != null && validTo == null) {
                pagingAndFiltering.addFilter("minmaxOptionalRange validity.from validity.to", validFrom);

                // search by date range
            } else if (validFrom != null && validTo != null) {
                pagingAndFiltering.addFilter("overlapOptionalRange validity.from validity.to", new Date[] { validFrom, validTo });
            }

            pagingAndFiltering.addFilter("disabled", false);

        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, OfferTemplate.class);

        Long totalCount = offerTemplateService.count(paginationConfig);

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<OfferTemplate> offers = offerTemplateService.list(paginationConfig);
            for (OfferTemplate offerTemplate : offers) {
                result.addOfferTemplate(fromOfferTemplate(offerTemplate, inheritCF, pagingAndFiltering.hasFieldOption("offerServiceTemplate"),
                    pagingAndFiltering.hasFieldOption("offerProductTemplate"), pagingAndFiltering.hasFieldOption("serviceChargeTemplate"),
                    pagingAndFiltering.hasFieldOption("productChargeTemplate"), pagingAndFiltering.hasFieldOption("loadAllowedDiscountPlan")));
            }
        }

        return result;
    }
}