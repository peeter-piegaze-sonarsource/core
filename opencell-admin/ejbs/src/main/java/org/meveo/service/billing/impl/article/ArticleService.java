package org.meveo.service.billing.impl.article;

import org.meveo.model.article.Article;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;

@Stateless
public class ArticleService extends BusinessService<Article> {

    public Article createArticle(WalletOperation walletOperation) {
        Article article = new Article(walletOperation.getCode(), walletOperation.getDescription(), walletOperation.getTaxClass(), walletOperation.getInvoiceSubCategory());
        article.setAccountingCode(walletOperation.getAccountingCode());
        create(article);
        return article;
    }
}
