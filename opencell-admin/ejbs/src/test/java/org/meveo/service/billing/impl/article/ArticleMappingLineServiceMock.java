package org.meveo.service.billing.impl.article;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;

public class ArticleMappingLineServiceMock extends ArticleMappingLineService {

    @Override
    public void create(ArticleMappingLine entity) throws BusinessException {
    }

    @Override
    protected ArticleMapping loadArticleMapping() {
        return new ArticleMapping();
    }
}
