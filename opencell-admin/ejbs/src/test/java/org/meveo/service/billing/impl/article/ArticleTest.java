package org.meveo.service.billing.impl.article;

import org.apache.commons.net.nntp.Article;
import org.junit.Test;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;

public class ArticleTest {

    @Test
    public void can_create_article() {
        AccountingArticle accountingArticle = new AccountingArticle("CODE", "Description", new TaxClass(), new InvoiceSubCategory());
    }
}
