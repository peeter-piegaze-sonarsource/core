package org.meveo.service.billing.impl.article;

import org.junit.Test;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleTest {

    @Test
    public void can_create_article() {
        TaxClass taxClass = new TaxClass();
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        AccountingArticle accountingArticle = new AccountingArticle("CODE", "Description", taxClass, invoiceSubCategory);

        assertThat(accountingArticle.getCode()).isEqualTo("CODE");
        assertThat(accountingArticle.getDescription()).isEqualTo("Description");
        assertThat(accountingArticle.getTaxClass()).isEqualTo(taxClass);
        assertThat(accountingArticle.getInvoiceSubCategory()).isEqualTo(invoiceSubCategory);
    }

    @Test
    public void can_create_article_family() {
        AccountingCode accountingCode = new AccountingCode();
        ArticleFamily articleFamily = new ArticleFamily("CODE", "Description", accountingCode);

        assertThat(articleFamily.getCode()).isEqualTo("CODE");
        assertThat(articleFamily.getDescription()).isEqualTo("Description");
        assertThat(articleFamily.getAccountingCode()).isEqualTo(accountingCode);
    }
}
