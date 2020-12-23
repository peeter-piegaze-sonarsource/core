package org.meveo.api.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductContextDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;

/**
 * @author Mbarek-Ay
 * @version 11.0
 */
@Stateless
public class AttributeApi extends BaseCrudApi<Attribute, AttributeDTO> {

	@Inject
	private AttributeService attributeService; 

	@Inject
	private GroupedAttributeService groupedAttributeService;

	@Inject
	private OfferTemplateService  offerTemplateService;
	
	@Inject
	private ProductVersionService  productVersionService;
	
	@Inject
	private ProductService  productService;

	@Override
	public Attribute create(AttributeDTO postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getGroupedAttributeCode())) {
			missingParameters.add("GroupedAttributeCode");
		}
		if (attributeService.findByCode(postData.getCode()) != null) {
			throw new EntityAlreadyExistsException(Attribute.class, postData.getCode());
		}

		handleMissingParametersAndValidate(postData);

		// check if groupedAttributes  exists
		GroupedAttributes groupedAttributes = groupedAttributeService.findByCode(postData.getGroupedAttributeCode());
		if (groupedAttributes == null) {
			throw new EntityDoesNotExistsException(GroupedAttributes.class, postData.getGroupedAttributeCode());
		}

		Attribute attribute = new Attribute();
		attribute.setCode(postData.getCode());
		attribute.setDescription(postData.getDescription());
		attribute.setGroupedAttributes(groupedAttributes);
		attribute.setPriority(postData.getPriority());
		attribute.setDisplay(postData.isDisplay());
		attribute.setMandatory(postData.isMandatory());
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setSequence(postData.getSequence());
		attribute.setAllowedValues(postData.getAllowedValues());
		attributeService.create(attribute);
		return attribute;
	}

	@Override
	public Attribute update(AttributeDTO postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getGroupedAttributeCode())) {
			missingParameters.add("GroupedAttributeCode");
		}

		Attribute attribute=attributeService.findByCode(postData.getCode());
		if (attribute== null) {
			throw new EntityDoesNotExistsException(Attribute.class, postData.getCode());
		}

		// check if groupedAttributes  exists
		GroupedAttributes groupedAttributes = groupedAttributeService.findByCode(postData.getGroupedAttributeCode());
		if (groupedAttributes == null) {
			throw new EntityDoesNotExistsException(GroupedAttributes.class, postData.getGroupedAttributeCode());
		}  
		attribute.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		attribute.setDescription(postData.getDescription());
		attribute.setGroupedAttributes(groupedAttributes);
		attribute.setPriority(postData.getPriority());
		attribute.setDisplay(postData.isDisplay());
		attribute.setMandatory(postData.isMandatory());
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setSequence(postData.getSequence());
		attribute.setAllowedValues(postData.getAllowedValues());
		attributeService.update(attribute);
		return attribute;
	}

	public AttributeDTO findByCode(String code) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		AttributeDTO result = new AttributeDTO();
		Attribute attribute = attributeService.findByCode(code);
		if (attribute == null) {
			throw new EntityDoesNotExistsException(Attribute.class, code);
		}
		result = new AttributeDTO(attribute);
		return result;
	}

	public void remove(String code) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		Attribute attribute=attributeService.findByCode(code);
		if (attribute== null) {
			throw new EntityDoesNotExistsException(Attribute.class, code);
		}
		attributeService.remove(attribute);

	} 

	public Attribute createOrUpdate(AttributeDTO postData) throws MeveoApiException, BusinessException {
		if (attributeService.findByCode(postData.getCode()) != null) {
			return update(postData);
		} else {
			return create(postData);
		}
	}
	
	
	
	public GetProductDtoResponse listPost(String productCode,String currentProductVersion,OfferContextDTO offerContextDto) { 
		if(Strings.isEmpty(productCode)) {
			missingParameters.add("productCode");
		} 
		if(Strings.isEmpty(currentProductVersion)) {
			missingParameters.add("currentProductVersion");
		} 

		Product product = productService.findByCode(productCode);
		if(product==null) {
			throw new EntityDoesNotExistsException(Product.class,productCode);
		}
		ProductDto productDto=new ProductDto(product);
		ProductVersion productVersion = productVersionService.findByProductAndVersion(productCode,Integer.parseInt(currentProductVersion));
		if(productVersion==null) {
			throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentProductVersion,"currentVersion");
		}
		ProductVersionDto productVersionDto=new ProductVersionDto(productVersion,true,false);
		productDto.setCurrentProductVersion(productVersionDto);
		GetProductDtoResponse result = new GetProductDtoResponse();
		result.setProductDto(productDto);    
		return result;
	}

	
 
	
}
