package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;

public class AccountingArticleDto extends BaseEntityDto {

	private static final long serialVersionUID = 1L;
	
    @NotNull
    @XmlAttribute(required = true)
    private String code;

	@XmlElement
	private String taxClassCode;

	@XmlElement
	private String invoiceSubCategoryCode;
	
	@XmlElement
	private String articleFamilyCode;

	@XmlElement
	private String accountingCodeCode;

	@XmlElement
	private String articleMappingLineCode;

	@XmlElement
	private String analyticCode1;

	@XmlElement
	private String analyticCode2;

	@XmlElement
	private String analyticCode3;

	/** The custom fields. */
	@XmlElement
	private CustomFieldsDto customFields;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTaxClassCode() {
		return taxClassCode;
	}

	public void setTaxClassCode(String taxClassCode) {
		this.taxClassCode = taxClassCode;
	}

	public String getInvoiceSubCategoryCode() {
		return invoiceSubCategoryCode;
	}

	public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
		this.invoiceSubCategoryCode = invoiceSubCategoryCode;
	}

	public String getArticleFamilyCode() {
		return articleFamilyCode;
	}

	public void setArticleFamilyCode(String articleFamilyCode) {
		this.articleFamilyCode = articleFamilyCode;
	}

	public String getAccountingCodeCode() {
		return accountingCodeCode;
	}

	public void setAccountingCodeCode(String accountingCodeCode) {
		this.accountingCodeCode = accountingCodeCode;
	}

	public String getArticleMappingLineCode() {
		return articleMappingLineCode;
	}

	public void setArticleMappingLineCode(String articleMappingLineCode) {
		this.articleMappingLineCode = articleMappingLineCode;
	}

	public String getAnalyticCode1() {
		return analyticCode1;
	}

	public void setAnalyticCode1(String analyticCode1) {
		this.analyticCode1 = analyticCode1;
	}

	public String getAnalyticCode2() {
		return analyticCode2;
	}

	public void setAnalyticCode2(String analyticCode2) {
		this.analyticCode2 = analyticCode2;
	}

	public String getAnalyticCode3() {
		return analyticCode3;
	}

	public void setAnalyticCode3(String analyticCode3) {
		this.analyticCode3 = analyticCode3;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
   
}