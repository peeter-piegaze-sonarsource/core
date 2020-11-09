package org.meveo.api.cpq;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.exception.ProductException;
import org.meveo.service.cpq.exception.ProductLineException;
import org.meveo.service.cpq.exception.ProductVersionException;
import org.meveo.service.crm.impl.CustomerBrandService;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Stateless
public class ProductApi extends BaseApi {


	private static final String PRODUCT_STATUS_NOT_FOUND = "Status (%d) not found!!";
	
	@Inject
	private ProductService productService;
	@Inject
	private ProductLineService productLineService;
	@Inject
	private CustomerBrandService brandService;
	@Inject
	private DiscountPlanService discountPlanService;
	@Inject
	private ProductLineApi productLineApi;
	
	@Inject
	private ProductVersionService productVersionService;
	
	/**
	 * @return ProductDto
	 * @throws ProductException
	 */
	public ProductDto addNewProduct(ProductDto productDto){
		if(Strings.isEmpty(productDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		Long idLineProdcut = productDto.getProductLine() != null ? idLineProdcut = productDto.getProductLine().getId() : null;
		String codeBrand = productDto.getBrand() != null ? productDto.getBrand().getCode() : null;
		var discountList = productDto.getDiscountList().stream().map(DiscountPlanDto::getCode).collect(Collectors.toSet());
		try {
			return new ProductDto(productService.create(productDto.getCode(), productDto.getLabel(), idLineProdcut, codeBrand,
															productDto.getReference(), productDto.getModel(), productDto.getModelChlidren(),
															discountList, productDto.isDiscountFlag()));
		} catch (ProductException e) {
			throw new BusinessException(e);
		}
	}
	
	/**
	 * @param productDto
	 * @throws ProductException
	 */
	public ProductDto updateProduct(ProductDto productDto){
		
		try {
			Product product = productService.findByCode(productDto.getCode());
			product.setDescription(productDto.getLabel());
			if(productDto.getProductLine() != null) {
				product.setProductLine(productLineService.findById(productDto.getProductLine().getId()));
			}
			if(productDto.getBrand() != null) {
				product.setBrand(brandService.findByCode(productDto.getBrand().getCode()));
			}
			product.setReference(productDto.getReference());
			product.setModel(productDto.getModel());
			product.setModelChlidren(productDto.getModelChlidren());
			
			if(productDto.getDiscountList() != null && !productDto.getDiscountList().isEmpty()) {
				Set<DiscountPlan> discountPlans  = new HashSet<DiscountPlan>(productDto.getDiscountList().stream().map(d -> {
					final DiscountPlan discount = discountPlanService.findByCode(d.getCode());
					return discount;
				}).collect(Collectors.toSet()));
				product.setDiscountList(discountPlans);
			}
			product.setDiscountFlag(productDto.isDiscountFlag());
			return new ProductDto(productService.updateProduct(product));
		} catch (ProductException e) {
			throw new MeveoApiException(e);
		}
	}
	
	/**
	 * update status of a product
	 * @param codeProduct
	 * @param status
	 * @return
	 * @throws ProductException when the status is unknown and the status 
	 */
	public ProductDto updateStatus(String codeProduct, ProductStatusEnum status){
		if(Strings.isEmpty(codeProduct)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		if(status == null)
			throw new MeveoApiException(String.format(PRODUCT_STATUS_NOT_FOUND, status));
		Product product;
		try {
			product = productService.findByCode(codeProduct);
			return new ProductDto(productService.updateStatus(product, status));
		} catch (ProductException e) {
			throw new MeveoApiException(e);
		}
	}
	
	/**
	 * @param code
	 * @return
	 * @throws ProductException
	 */
	public ProductDto findByCode(String code){
		if(Strings.isEmpty(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		return new ProductDto(productService.findByCode(code));
	}
	
	

	/**
	 * @param dto
	 * @return
	 */
	public ProductLineDto createProductLine(ProductLineDto dto){
		return new ProductLineDto(productLineApi.createProductLine(dto));
	}

	/**
	 * @param dto
	 * @return
	 */
	public ProductLineDto updateProductLine(ProductLineDto dto){
			return new ProductLineDto(productLineApi.updateProductLine(dto));
	}

	/**
	 * @param code
	 * @return
	 */
	public ProductLineDto findProductLineByCode(String code) {
		return new ProductLineDto(productLineService.findByCode(code));
	}
	
	
	public ProductVersion createProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException, ProductException {
        String description = postData.getShortDescription();
        String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        if (StringUtils.isBlank(description)) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(productCode)) {
            missingParameters.add("productCode");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        handleMissingParametersAndValidate(postData);
        Product product = productService.findByCode(productCode);
        if (product == null) {
            throw new EntityDoesNotExistsException(Product.class,productCode,"productCode");
        }
        ProductVersion  productVersion= new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setShortDescription(description);
        productVersion.setLongDescription(postData.getLongDescription());
        productVersion.setCurrentVersion(currentVersion);
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setEndDate(postData.getEndDate());
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setStatusDate(Calendar.getInstance().getTime());
        productVersionService.create(productVersion);
        return productVersion;
    }
       /**
     * Updates a product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public ProductVersion updateProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
        String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        if (StringUtils.isBlank(productCode)) {
            missingParameters.add("productCode");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
       ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
		} catch (ProductException e) {
			throw new MeveoApiException(e);
		}
       if(productVersion==null) {
           throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
       }
        productVersion.setShortDescription(postData.getShortDescription());
        productVersion.setLongDescription(postData.getLongDescription());
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setEndDate(postData.getEndDate());
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setStatus(postData.getStatus());
        try {
			productVersionService.updateProductVersion(productVersion);
		} catch (ProductVersionException | ProductException e) {
			throw new MeveoApiException(e);
		}
        return productVersion;
    }
    /**
     * Delete a product version Entity
     *
     * @param productCode and currentVersion
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public void removeProductVersion(String productCode,int currentVersion){
    	  if (StringUtils.isBlank(productCode)) {
              missingParameters.add("productCode");
          }
          if (StringUtils.isBlank(currentVersion)) {
              missingParameters.add("currentVersion");
          }
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        productVersionService.removeProductVersion(productVersion);
		} catch (ProductException | ProductVersionException e) {
			throw new MeveoApiException(e);
		}
    }
    /**
     * Duplicate a product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public ProductVersionDto duplicateProductVersion(String productCode, int currentVersion)  throws MeveoApiException, BusinessException  { 
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        productVersionService.duplicate(productVersion);
	        return new ProductVersionDto(productVersion);
		} catch (ProductException | ProductVersionException e) {
			throw new MeveoApiException(e);
		}
    }
    /**
     * Change status product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public ProductVersionDto UpdateProductVersionStatus (String productCode, int currentVersion,VersionStatusEnum status)  throws MeveoApiException, BusinessException { 
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        productVersionService.updateProductVersionStatus(productVersion,status);
	        return new ProductVersionDto(productVersion);
		} catch (ProductException | ProductVersionException e) {
			throw new MeveoApiException(e);
		}
    }
    
    
    /**
     * Creates or updates product version based on the product code and current version.
     * 
     * @param postData posted data.
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException 
     * @throws ProductVersionException 
     */
    public ProductVersion createOrUpdateProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
    	String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if (productVersion == null) {
	            return createProductVersion(postData);
	        } else {
	            return updateProductVersion(postData);
	        }
		} catch (ProductException e) {
			throw new MeveoApiException(e);
		} 

    }
	
}
