package org.meveo.api.catalog;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.function.BiFunction;

@Stateless
public class PricePlanMatrixColumnApi extends BaseCrudApi<PricePlanMatrixColumn, PricePlanMatrixColumnDto> {

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;
    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private ProductService productService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private AttributeService attributeService;

    @Override
    public PricePlanMatrixColumn create(PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {

        checkMissingParameters(dtoData);
        
        if(Strings.isEmpty(dtoData.getAttributeCode())  && Strings.isEmpty(dtoData.getElValue())) {
        	throw new MeveoApiException("Attribute or ElValue should be set");
        }

        if(pricePlanMatrixColumnService.findByCode(dtoData.getCode()) != null) {
            throw new EntityAlreadyExistsException(PricePlanMatrixColumn.class, dtoData.getCode());
        }

        PricePlanMatrixColumn pricePlanMatrixColumn = new PricePlanMatrixColumn();
        if(!Strings.isEmpty(dtoData.getAttributeCode())){
        	Attribute attribute = attributeService.findByCode(dtoData.getAttributeCode());
        	pricePlanMatrixColumn.setAttribute(attribute);
        }
        pricePlanMatrixColumn.setRange(dtoData.getRange());
        pricePlanMatrixColumn.setType(dtoData.getType());
        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn);


        pricePlanMatrixColumnService.create(pricePlanMatrixColumn);

        return pricePlanMatrixColumn;
    }

    @Override
    public PricePlanMatrixColumn update(PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {
        checkMissingParameters(dtoData);

        PricePlanMatrixColumn pricePlanMatrixColumn = loadEntityByCode(pricePlanMatrixColumnService, dtoData.getCode(), PricePlanMatrixColumn.class);

        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn);

        return pricePlanMatrixColumnService.update(pricePlanMatrixColumn);
    }

    public void removePricePlanColumn(String code){
        pricePlanMatrixColumnService.removePricePlanColumn(code);
    }

    @Override
    protected BiFunction<PricePlanMatrixColumn, CustomFieldsDto, PricePlanMatrixColumnDto> getEntityToDtoFunction() {
        return (a, b) -> new PricePlanMatrixColumnDto(a);
    }

    private void checkMissingParameters(PricePlanMatrixColumnDto dtoData) {
        if (StringUtils.isBlank(dtoData.getPricePlanMatrixCode())) {
            missingParameters.add("pricePlanMatrixCode");
        }
        if (StringUtils.isBlank(dtoData.getPricePlanMatrixVersion())) {
            missingParameters.add("pricePlanMatrixVersion");
        }
        if (StringUtils.isBlank(dtoData.getOfferTemplateCode())) {
            missingParameters.add("offerTemplateCode");
        }
        if (StringUtils.isBlank(dtoData.getPosition())) {
            missingParameters.add("position");
        }
        if (StringUtils.isBlank(dtoData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dtoData.getProductCode())) {
            missingParameters.add("productCode");
        }

        handleMissingParametersAndValidate(dtoData);
    }

    private void populatePricePlanMatrixColumn(PricePlanMatrixColumnDto dtoData, PricePlanMatrixColumn pricePlanMatrixColumn) {
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        if(pricePlanMatrixVersion == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixCode(), "pricePlanMatrixCode", ""+dtoData.getPricePlanMatrixVersion(), "pricePlanMatrixVersion");
        }

        if(Strings.isEmpty(dtoData.getAttributeCode())  && Strings.isEmpty(dtoData.getElValue())) {
        	throw new MeveoApiException("Attribute or ElValue should be set");
        }
        
    	pricePlanMatrixColumn.setAttribute(attributeService.findByCode(dtoData.getAttributeCode()));
        pricePlanMatrixColumn.setElValue(dtoData.getElValue());
        pricePlanMatrixColumn.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixColumn.setProduct(loadEntityByCode(productService, dtoData.getProductCode(), Product.class));
        pricePlanMatrixColumn.setOfferTemplate(loadEntityByCode(offerTemplateService, dtoData.getOfferTemplateCode(), OfferTemplate.class));
        pricePlanMatrixColumn.setCode(dtoData.getCode());
        pricePlanMatrixColumn.setPosition(dtoData.getPosition());
    }
}
