package org.meveo.api.cpq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.ContractItemDto;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Tarik F.
 * @version 10.0
 *
 */
@Stateless
public class ContractApi extends BaseApi{

	@Inject
	private ContractService contractService;
	@Inject
	private SellerService sellerService;
	@Inject
	private BillingAccountService billingAccountService;
	@Inject
	private CustomerAccountService customerAccountService;
	@Inject 
	private CustomerService customerService;
	@Inject
	private ContractItemService contractItemService;
	@Inject
	private ServiceTemplateService serviceTemplateService;
	@Inject
	private OfferTemplateService offerTemplateService;
	@Inject
	private ProductService productService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	@Inject
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
	
	private static final String CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN = "Date end (%s) must be great than date begin (%s)";
	private static final String CONTRACt_STAT_DIFF_TO_DRAFT = "Only Draft status of contract can be edit";
	
	public Long CreateContract(ContractDto dto) {
		// check mandatory param
		checkParams(dto);
		//check if date end great than date begin
		if(dto.getEndDate().compareTo(dto.getBeginDate()) < 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			throw new BusinessApiException(String.format(CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN, format.format(dto.getEndDate()), format.format(dto.getBeginDate())));
		}
		// check if contract already exist
		Contract contract = contractService.findByCode(dto.getCode());
		if(contract != null)
			throw new EntityAlreadyExistsException(Contract.class, dto.getCode());
		// create new contract
		contract = new Contract();
		contract.setCode(dto.getCode());
		contract.setBeginDate(dto.getBeginDate());
		contract.setEndDate(dto.getEndDate());
		contract.setContractDate(dto.getContractDate());
		contract.setRenewal(dto.isRenewal());
		contract.setContractDuration(dto.getContractDuration());
		//retrieve seller
		final Seller seller = sellerService.findByCode(dto.getSellerCode());
//		if(seller == null)
//			throw new EntityDoesNotExistsException(Seller.class, dto.getSellerCode());
		contract.setSeller(seller);
		// get billing account if it exist
		if(!Strings.isEmpty(dto.getBillingAccountCode())) {
				contract.setBillingAccount(billingAccountService.findByCode(dto.getBillingAccountCode()));
		}
		// get customer account if it exist
		if(!Strings.isEmpty(dto.getCustomerAccountCode())) {
			contract.setCustomerAccount(customerAccountService.findByCode(dto.getCustomerAccountCode()));
		}
		// get customer if it exist
		if(!Strings.isEmpty(dto.getCustomerCode())) {
			contract.setCustomer(customerService.findByCode(dto.getCustomerCode()));
		}
		try {
			contractService.create(contract);
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
		return contract.getId();
	}
	
	public void updateContract(ContractDto dto) {
		// check mandatory param
		checkParams(dto);
		//check if date end great than date begin
		if(dto.getEndDate().compareTo(dto.getBeginDate()) < 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			throw new BusinessApiException(String.format(CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN, format.format(dto.getEndDate()), format.format(dto.getBeginDate())));
		}
		//get the current contract
		Contract contract = contractService.findByCode(dto.getCode());
		if(contract == null)
			throw new EntityDoesNotExistsException(Contract.class, dto.getCode());
		//check the status of the contract
		if(!ProductStatusEnum.DRAFT.equals(contract.getStatus())) {
			throw new MeveoApiException(CONTRACt_STAT_DIFF_TO_DRAFT);
		}
		contract.setCode(dto.getCode());
		contract.setBeginDate(dto.getBeginDate());
		contract.setEndDate(dto.getEndDate());
		contract.setContractDate(dto.getContractDate());
		contract.setRenewal(dto.isRenewal());
		contract.setContractDuration(dto.getContractDuration());
		
		if(contract.getSeller() != null && !contract.getSeller().getCode().equals(dto.getSellerCode())) {
			final Seller seller = sellerService.findByCode(dto.getSellerCode());
			if(seller == null)
				throw new EntityDoesNotExistsException(Seller.class, dto.getSellerCode());
			contract.setSeller(seller);
		}
		if(!Strings.isEmpty(dto.getBillingAccountCode())) {
			contract.setBillingAccount(billingAccountService.findByCode(dto.getBillingAccountCode()));
		}else {
			contract.setBillingAccount(null);
		}
		// get customer account if it exist
		if(!Strings.isEmpty(dto.getCustomerAccountCode())) {
			contract.setCustomerAccount(customerAccountService.findByCode(dto.getCustomerAccountCode()));
		}else {
			contract.setCustomerAccount(null);
		}
		// get customer if it exist
		if(!Strings.isEmpty(dto.getCustomerCode())) {
			contract.setCustomer(customerService.findByCode(dto.getCustomerCode()));
		}else {
			contract.setCustomer(null);
		}
		try {
			contractService.updateContract(contract);
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
	}

	public void deleteContract(String codeContract) {
		try {
			contractService.deleteContractByCode(codeContract);
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
	}
	
	public ContractDto findContract(String contractCode) {
		final Contract contract = contractService.findByCode(contractCode);
		if(contract == null)
			throw new EntityDoesNotExistsException(Contract.class, contractCode);
		return new ContractDto(contract);
	}
	
	public List<ContractDto> findContract(ContractAccountLevel contractAccountLevel, String accountCode) {
		if(contractAccountLevel == null)
			missingParameters.add("contractAccountLevel");
		if(Strings.isEmpty(accountCode))
			missingParameters.add("accountCode");
		handleMissingParameters();
		return contractService.findByBillingAccountLevel(contractAccountLevel, accountCode)
													.stream().map(c -> new ContractDto(c)).collect(Collectors.toList());
	}

    private static final String DEFAULT_SORT_ORDER_ID = "id";

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contracts.contract", 
    			itemPropertiesToFilter = { 
    							@FilterProperty(property = "sellerCode", entityClass = Seller.class),
    							@FilterProperty(property = "billingAccountCode", entityClass = BillingAccount.class),
    							@FilterProperty(property = "customerAccountCode", entityClass = CustomerAccount.class),
    							@FilterProperty(property = "customerCode", entityClass = Customer.class) }, totalRecords = "contract.listSize")
    public ContractListResponsDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, Contract.class);

        Long totalCount = contractService.count(paginationConfiguration);
        ContractListResponsDto result = new ContractListResponsDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        
        if(totalCount > 0) {
        	contractService.list(paginationConfiguration).stream().forEach( c -> {
        		result.getContracts().getContracts().add(new ContractDto(c));
        	});
        }
    	return result;
    }
    
    public Long createContractLine(ContractItemDto contractItemDto) {
    	checkParams(contractItemDto);
    	ContractItem item = contractItemService.findByCode(contractItemDto.getCode());
    	if(item != null)
    		throw new EntityAlreadyExistsException(ContractItem.class, contractItemDto.getCode());
    	item = new ContractItem();
    	final Contract contract = contractService.findByCode(contractItemDto.getContractCode());
    	if(contract == null)
    		throw new EntityDoesNotExistsException(Contract.class, contractItemDto.getContractCode());
    	final ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(contractItemDto.getServiceTemplateCode());
    	if(serviceTemplate == null)
    		throw new EntityDoesNotExistsException(ServiceTemplate.class, contractItemDto.getServiceTemplateCode());
    	final PricePlanMatrix planMatrix = pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode());
    	if(planMatrix == null)
    		throw new EntityDoesNotExistsException(PricePlanMatrix.class, contractItemDto.getPricePlanCode());
    	
    	item.setCode(contractItemDto.getCode());
    	item.setContract(contract);
    	item.setServiceTemplate(serviceTemplate);
		item.setPricePlan(planMatrix);
		
    	if(!Strings.isEmpty(contractItemDto.getServiceTemplateCode()))
    		item.setOfferTemplate(offerTemplateService.findByCode(contractItemDto.getServiceTemplateCode()));
    	if(!Strings.isEmpty(contractItemDto.getProductCode()))
    		item.setProduct(productService.findByCode(contractItemDto.getProductCode()));
    	if(!Strings.isEmpty(contractItemDto.getPricePlanCode()))
    		item.setPricePlan(pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode()));
    	if(!Strings.isEmpty(contractItemDto.getChargeTemplateCode()))
    		item.setChargeTemplate(chargeTemplateService.findByCode(contractItemDto.getChargeTemplateCode()));
    	item.setRate(contractItemDto.getRate());
    	item.setAmountWithoutTax(contractItemDto.getAmountWithoutTax());
    	
    	try {
    		contractItemService.create(item);
    		return item.getId();
    	}catch(BusinessException e) {
    		throw new MeveoApiException(e);
    	}
    }
    
    public void updateContractLine(ContractItemDto contractItemDto) {
    	if(Strings.isEmpty(contractItemDto.getCode()))
			missingParameters.add("code");
    	handleMissingParameters();
    	final ContractItem item = contractItemService.findByCode(contractItemDto.getCode());
    	if(item == null)
    		throw new EntityDoesNotExistsException(ContractItem.class, contractItemDto.getCode());
    	
    	if(!Strings.isEmpty(contractItemDto.getContractCode())) {
	    	final Contract contract = contractService.findByCode(contractItemDto.getContractCode());
	    	if(contract == null)
	    		throw new EntityDoesNotExistsException(Contract.class, contractItemDto.getContractCode());
	    	item.setContract(contract);
    	}
    	if(!Strings.isEmpty(contractItemDto.getServiceTemplateCode())) {
	    	final ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(contractItemDto.getServiceTemplateCode());
	    	if(serviceTemplate == null)
	    		throw new EntityDoesNotExistsException(ServiceTemplate.class, contractItemDto.getServiceTemplateCode());
	    	item.setServiceTemplate(serviceTemplate);
    	}
    	if(!Strings.isEmpty(contractItemDto.getPricePlanCode())) {
	    	final PricePlanMatrix planMatrix = pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode());
	    	if(planMatrix == null)
	    		throw new EntityDoesNotExistsException(PricePlanMatrix.class, contractItemDto.getPricePlanCode());
			item.setPricePlan(planMatrix);
    	}

		
    	if(!Strings.isEmpty(contractItemDto.getProductCode()))
    		item.setProduct(productService.findByCode(contractItemDto.getProductCode()));
    	if(!Strings.isEmpty(contractItemDto.getChargeTemplateCode()))
    		item.setChargeTemplate(chargeTemplateService.findByCode(contractItemDto.getChargeTemplateCode()));
    	item.setRate(contractItemDto.getRate());
    	item.setAmountWithoutTax(contractItemDto.getAmountWithoutTax());
    	
    	try {
    		contractItemService.updateContractItem(item);
    	}catch(BusinessException e) {
    		throw new MeveoApiException(e);
    	}
    	
    }
    
    public void deleteContractLine(String contractItemCode) {
    	if(Strings.isEmpty(contractItemCode))
    		missingParameters.add("contractCode");
    	handleMissingParameters();
    	contractItemService.deleteContractItem(contractItemCode);
    }
    
    public ContractItemDto getContractLines(String contractItemCode) {
    	final ContractItem item = contractItemService.findByCode(contractItemCode);
    	if(item == null)
    		throw new EntityDoesNotExistsException(ContractItem.class, contractItemCode);
    	return new ContractItemDto(item);
    }
    
	
	
	private void checkParams(ContractItemDto contractItemDto) {
    	if(Strings.isEmpty(contractItemDto.getContractCode()))
    		missingParameters.add("contractCode");
    	if(Strings.isEmpty(contractItemDto.getCode()))
			missingParameters.add("code");
    	if(Strings.isEmpty(contractItemDto.getServiceTemplateCode()))
			missingParameters.add("serviceTemplateCode");
    	if(Strings.isEmpty(contractItemDto.getPricePlanCode()))
			missingParameters.add("pricePlanCode");
    		
    	handleMissingParameters();
	}
	
	
	private void checkParams(ContractDto dto) {
		if(Strings.isEmpty(dto.getCode()))
			missingParameters.add("code");
		if(Strings.isEmpty(dto.getSellerCode()))
			missingParameters.add("sellerCode");
		if(dto.getContractDate() == null)
			missingParameters.add("contractDate");
		if(dto.getBeginDate() == null)
			missingParameters.add("beginDate");
		if(dto.getEndDate() == null)
			missingParameters.add("endDate");
		handleMissingParameters();
		
	}
}
