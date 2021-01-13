package org.meveo.apiv2.article.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.article.AccountingArticleService1;
import org.meveo.service.billing.impl.article.ArticleFamilyService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.tax.TaxClassService;

public class AccountingArticleApiService implements ApiService<AccountingArticle> {

    private List<String> fetchFields;
    @Inject
    private AccountingArticleService1 accountingArticleService1;
    @Inject
    private AccountingCodeService accountingCodeService;
    @Inject
    private TaxClassService taxClassService;
    @Inject
    private ArticleFamilyService articleFamilyService;
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("taxClass", "invoiceSubCategory", "articleFamily", "accountingCode", "articleMappingLine");
    }
    
    @Override
    public AccountingArticle create(AccountingArticle accountingArticle) {
        TaxClass taxClass = taxClassService.findById(accountingArticle.getTaxClass().getId());
        if(taxClass == null)
            throw new BadRequestException("No tax class found with id: " + accountingArticle.getTaxClass().getId());
        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(accountingArticle.getInvoiceSubCategory().getId());
        if(invoiceSubCategory == null)
            throw new BadRequestException("No invoice sub category found with id: " + accountingArticle.getInvoiceSubCategory().getId());
        accountingArticle.setTaxClass(taxClass);
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        if(accountingArticle.getAccountingCode() != null){
            AccountingCode accountingCode = accountingCodeService.findById(accountingArticle.getAccountingCode().getId());
            if(accountingCode == null)
                throw new BadRequestException("No accounting code found with id: " + accountingArticle.getAccountingCode().getId());
            accountingArticle.setAccountingCode(accountingCode);
        }
        if(accountingArticle.getArticleFamily() != null){
            ArticleFamily articleFamily = articleFamilyService.findById(accountingArticle.getArticleFamily().getId());
            if(articleFamily == null)
                throw new BadRequestException("No article family found with id: " + accountingArticle.getArticleFamily().getId());
        }

        accountingArticleService1.create(accountingArticle);
        return accountingArticle;
    }

    @Override
    public List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), filter, null, fetchFields, null, null);
        return accountingArticleService1.list(paginationConfiguration);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return accountingArticleService1.count(paginationConfiguration);
    }

    @Override
    public Long getCount(Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, filter, null, fetchFields, null, null);
        return accountingArticleService1.count(paginationConfiguration);
    }

    @Override
    public Optional<AccountingArticle> findById(Long id) {
        return Optional.of(accountingArticleService1.findById(id));
    }

    @Override
    public Optional<AccountingArticle> update(Long id, AccountingArticle baseEntity) {
        Optional<AccountingArticle> accountingArticleOtional = findById(id);
        if(!accountingArticleOtional.isPresent()) {
            return Optional.empty();
        }
        AccountingArticle accountingArticle = accountingArticleOtional.get();
        if(baseEntity.getTaxClass() != null && baseEntity.getTaxClass().getId() != null) {
	        TaxClass taxClass = taxClassService.findById(baseEntity.getTaxClass().getId());
	        if(taxClass == null)
	            throw new BadRequestException("No tax class found with id: " + baseEntity.getTaxClass().getId());
	        accountingArticle.setTaxClass(taxClass);
        }
        if(baseEntity.getInvoiceSubCategory() != null && baseEntity.getInvoiceSubCategory().getId() != null) {
	        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(baseEntity.getInvoiceSubCategory().getId());
	        if(invoiceSubCategory == null)
	            throw new BadRequestException("No invoice sub category found with id: " + baseEntity.getInvoiceSubCategory().getId());
	        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        }
        if(baseEntity.getAccountingCode() != null){
            AccountingCode accountingCode = accountingCodeService.findById(baseEntity.getAccountingCode().getId());
            if(accountingCode == null)
                throw new BadRequestException("No accounting code found with id: " + baseEntity.getAccountingCode().getId());
            accountingArticle.setAccountingCode(accountingCode);
        }
        if(baseEntity.getArticleFamily() != null){
            ArticleFamily articleFamily = articleFamilyService.findById(baseEntity.getArticleFamily().getId());
            if(articleFamily == null)
                throw new BadRequestException("No article family found with id: " + baseEntity.getArticleFamily().getId());
        }
        accountingArticleService1.update(accountingArticle);
        
        return accountingArticleOtional;
    }

    @Override
    public Optional<AccountingArticle> patch(Long id, AccountingArticle baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<AccountingArticle> delete(Long id) {
       Optional<AccountingArticle> accountingArticle = findById(id);
       if(accountingArticle.isPresent()) {
    	   try {
    		   accountingArticleService1.remove(accountingArticle.get());
           } catch (Exception e) {
               throw new BadRequestException(e);
           }
       }
       return accountingArticle;
    }
    
    @Override
    public Optional<AccountingArticle> delete(String code) {
    	Optional<AccountingArticle> accountingArticle = findByCode(code);
        if(accountingArticle.isPresent()) {
     	   try {
     		   accountingArticleService1.remove(accountingArticle.get());
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return accountingArticle;
    }
    
    @Override
    public Optional<AccountingArticle> findByCode(String code) {
    	AccountingArticle accountingArticle = accountingArticleService1.findByCode(code);
    	if(accountingArticle == null)
    		throw new BadRequestException("No Account Article class found with code: " + code);
    	return Optional.ofNullable(accountingArticle);
    }

	@Override
	public List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}
}