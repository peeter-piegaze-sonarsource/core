package org.meveo.service.billing.impl.article;

import org.junit.Test;
import org.meveo.model.article.Article;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.tax.TaxClass;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleServiceTest {

    @Test
    public void canCreateAnArticleFromWalletOperation() {
        ArticleService articleService = new ArticleServiceMock();
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        TaxClass taxClass = new TaxClass();
        AccountingCode accountingCode = new AccountingCode();

        WalletOperation walletOperation = createWalletOperation(invoiceSubCategory, taxClass, accountingCode);

        Article article = articleService.createArticle(walletOperation);

        assertThat(article.getTaxClass()).isEqualTo(taxClass);
        assertThat(article.getInvoiceSubCategory()).isEqualTo(invoiceSubCategory);
        assertThat(article.getAccountingCode()).isEqualTo(accountingCode);


    }

    private WalletOperation createWalletOperation(InvoiceSubCategory invoiceSubCategory, TaxClass taxClass, AccountingCode accountingCode) {
        WalletOperation walletOperation = new WalletOperation();
        walletOperation.setTaxClass(taxClass);
        walletOperation.setInvoiceSubCategory(invoiceSubCategory);
        walletOperation.setAccountingCode(accountingCode);
        return walletOperation;
    }
}
