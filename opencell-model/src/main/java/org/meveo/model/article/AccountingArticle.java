package org.meveo.model.article;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.tax.TaxClass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Map;

@Entity@CustomFieldEntity(cftCodePrefix = "Article")
@Table(name = "billing_article")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_seq"), })
public class AccountingArticle extends BusinessEntity {

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "tax_class_id")
    private TaxClass taxClass;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "article_family_id")
    private ArticleFamily articleFamily;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @OneToOne(mappedBy = "article")
    private ArticleMappingLine articleMappingLine;

    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    private AccountingArticle() {
    }

    public AccountingArticle(String code, String description, TaxClass taxClass, InvoiceSubCategory invoiceSubCategory) {
        this.code = code;
        this.description = description;
        this.taxClass = taxClass;
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public TaxClass getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public ArticleFamily getArticleFamily() {
        return articleFamily;
    }

    public void setArticleFamily(ArticleFamily articleFamily) {
        this.articleFamily = articleFamily;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    public ArticleMappingLine getArticleMappingLine() {
        return articleMappingLine;
    }

    public void setArticleMappingLine(ArticleMappingLine articleMappingLine) {
        this.articleMappingLine = articleMappingLine;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }
}
