package org.meveo.service.article;

import org.junit.Before;
import org.junit.Test;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleMappingLineServiceTest {

    private ArticleMappingLineService articleMappingLineService;

    @Before
    public void setUp() throws Exception {
        articleMappingLineService = new ArticleMappingLineService();
    }

    @Test
    public void article_mapping_line_match_rt_if_all_conditions_are_null() {
        ArticleMappingLine articleMappingLine = createArticleMappingLine();
        RatedTransaction ratedTransaction = new RatedTransaction();
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isTrue();
    }

    @Test
    public void article_mapping_line_is_not_match_if_params_not_equal() {
        ArticleMappingLine articleMappingLine = createArticleMappingLine();
        RatedTransaction ratedTransaction = new RatedTransaction();

        articleMappingLine.setParameter1("Param1");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isFalse();
        ratedTransaction.setParameter1("Param1");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isTrue();

        articleMappingLine.setParameter2("Param2");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isFalse();
        ratedTransaction.setParameter2("Param2");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isTrue();

        articleMappingLine.setParameter3("Param3");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isFalse();
        ratedTransaction.setParameter3("Param3");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isTrue();
    }

    @Test
    public void article_mapping_line_match_if_el_match() {
        ArticleMappingLine articleMappingLine = createArticleMappingLine();
        articleMappingLine.setMappingKelEL("#{ratedTransaction.getCode().equals('CODE')}");
        RatedTransaction ratedTransaction = new RatedTransaction();

        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isFalse();
        ratedTransaction.setCode("CODE");
        assertThat(articleMappingLineService.match(articleMappingLine, ratedTransaction)).isTrue();
    }

    private ArticleMappingLine createArticleMappingLine() {
        return new ArticleMappingLine(new AccountingArticle("CODE", "Description", new TaxClass(), new InvoiceSubCategory()), new ArticleMapping(1L));
    }
}
