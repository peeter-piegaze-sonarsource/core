package org.meveo.model.article;

import org.junit.Test;
import org.meveo.model.article.Article;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleTest {

    @Test
    public void code_description_texClass_and_invoiceSubCategory_are_mandatory() {
        Article article = new Article("CODE", "Description", new TaxClass(), new InvoiceSubCategory());

        assertThat(article.getCode()).isEqualTo("CODE");
        assertThat(article.getDescription()).isEqualTo("Description");
        assertThat(article.getTaxClass()).isNotNull();
        assertThat(article.getInvoiceSubCategory()).isNotNull();
    }
}
