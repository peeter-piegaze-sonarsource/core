package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ArticleMappingLine;
import org.meveo.apiv2.article.resource.ArticleMappingLineResource;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

@Stateless
public class ArticleMappingLineResourceImpl implements ArticleMappingLineResource {

    private ArticleMappingLineMapper mapper = new ArticleMappingLineMapper();
    @Override
    public Response createArticleMappingLine(ArticleMappingLine articleMappingLine) {
        return null;
    }
}
