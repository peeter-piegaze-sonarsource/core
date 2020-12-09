package org.meveo.service.billing.impl.article;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IBillableEntity;
import org.meveo.model.article.Article;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.service.base.BusinessService;
import org.meveo.service.filter.FilterService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

    @Inject
    private ArticleMappingService articleMappingService;

    @Inject
    private FilterService filterService;

    public ArticleMappingLine map(RatedTransaction ratedTransaction, Article article) {
        ArticleMappingLine articleMappingLine = new ArticleMappingLine();

        ArticleMapping articleMapping = loadArticleMapping();

        articleMappingLine.setArticle(article);
        articleMappingLine.setRatedTransaction(ratedTransaction);
        articleMappingLine.setCode(article.getCode() + "_" + ratedTransaction);
        articleMappingLine.setServiceCode(ratedTransaction.getServiceInstance());
        articleMappingLine.setChargeAndProductCode(ratedTransaction.getChargeInstance());
        articleMappingLine.setArticleMapping(articleMapping);
        ratedTransaction.setArticle(article);

        return articleMappingLine;
    }

    protected ArticleMapping loadArticleMapping() {
        return articleMappingService.findByCode("DEF_ART_MAP");
    }

    public List<ArticleMappingLine> listOfArticleMappingLineToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Filter articleMappingListFilter, int rtPageSize) throws BusinessException {

        if (articleMappingListFilter != null) {
            return (List<ArticleMappingLine>) filterService.filteredListAsObjects(articleMappingListFilter, null);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("ArticleMappingLine.listToInvoiceBySubscription", ArticleMappingLine.class).setParameter("subscriptionId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("ArticleMappingLine.listToInvoiceByBillingAccount", ArticleMappingLine.class).setParameter("billingAccountId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("ArticleMappingLine.listToInvoiceByOrderNumber", ArticleMappingLine.class).setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();
        }

        return new ArrayList<>();
    }
}
