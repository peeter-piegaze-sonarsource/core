package org.meveo.service.billing.impl.article;

import org.junit.Test;
import org.meveo.model.article.Article;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.tax.TaxClass;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleMappingLineServiceTest {

    @Test
    public void can_map_article_to_rt_if_no_mapping_is_provided() {
        ArticleMappingLineService articleMappingLineService = new ArticleMappingLineServiceMock();

        RatedTransaction ratedTransaction = new RatedTransaction();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setCode("SRV_INSTANCE_CODE");
        ratedTransaction.setServiceInstance(serviceInstance);
        RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
        chargeInstance.setCode("REC_CHARGE");
        ratedTransaction.setChargeInstance(chargeInstance);
        Article article = new Article("CODE", "DESC", new TaxClass(), new InvoiceSubCategory());

        ArticleMappingLine articleMappingLine = articleMappingLineService.map(ratedTransaction, article);

        assertThat(ratedTransaction.getArticle()).isEqualTo(article);
        assertThat(articleMappingLine.getChargeCode()).isEqualTo("REC_CHARGE");
        assertThat(articleMappingLine.getServiceCode()).isEqualTo("SRV_INSTANCE_CODE");
        assertThat(articleMappingLine.getProductCode()).isNull();
        assertThat(articleMappingLine.getArticleMapping()).isNotNull();

    }
}
