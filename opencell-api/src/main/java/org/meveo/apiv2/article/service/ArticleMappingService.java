package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.ArticleMapping;

import java.util.List;
import java.util.Optional;

public class ArticleMappingService implements ApiService<ArticleMapping> {

    @Override
    public List<ArticleMapping> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<ArticleMapping> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public ArticleMapping create(ArticleMapping baseEntity) {
        return null;
    }

    @Override
    public Optional<ArticleMapping> update(Long id, ArticleMapping baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMapping> patch(Long id, ArticleMapping baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMapping> delete(Long id) {
        return Optional.empty();
    }
}
