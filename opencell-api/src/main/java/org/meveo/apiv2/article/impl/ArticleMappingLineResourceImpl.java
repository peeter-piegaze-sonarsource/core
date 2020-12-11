package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ArticleMappingLine;
import org.meveo.apiv2.article.ImmutableArticleMapping;
import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.article.resource.ArticleMappingLineResource;
import org.meveo.apiv2.article.resource.ArticleMappingResource;
import org.meveo.apiv2.article.service.ArticleMappingLineService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class ArticleMappingLineResourceImpl implements ArticleMappingLineResource {

    @Inject
    private ArticleMappingLineService articleMappingLineService;

    private ArticleMappingLineMapper mapper = new ArticleMappingLineMapper();

    @Override
    public Response createArticleMappingLine(ArticleMappingLine articleMappingLine) {
        org.meveo.model.article.ArticleMappingLine articleMappingLineEntity = articleMappingLineService.create(mapper.toEntity(articleMappingLine));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingLineResource.class, articleMappingLineEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(articleMappingLineEntity)))
                .build();
    }

    private org.meveo.apiv2.article.ArticleMappingLine toResourceOrderWithLink(org.meveo.apiv2.article.ArticleMappingLine articleMappingLineResource) {
        return ImmutableArticleMappingLine.copyOf(articleMappingLineResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(ArticleMappingLineResource.class)
                                .withId(articleMappingLineResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }
}
