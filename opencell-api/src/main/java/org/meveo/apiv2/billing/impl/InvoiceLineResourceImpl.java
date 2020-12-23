package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.article.resource.ArticleMappingLineResource;
import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.billing.InvoiceLine;
import org.meveo.apiv2.billing.resource.InvoiceLineResource;
import org.meveo.apiv2.billing.service.InvoiceLineApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class InvoiceLineResourceImpl  implements InvoiceLineResource {

    @Inject
    private InvoiceLineApiService invoiceLineApiService;
    private InvoiceLineMapper mapper;

    @Override
    public Response createInvoiceLine(InvoiceLine invoiceLine) {
        org.meveo.model.billing.InvoiceLine invoiceLineEntity = invoiceLineApiService.create(mapper.toEntity(invoiceLine));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingLineResource.class, invoiceLineEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(invoiceLineEntity)))
                .build();
    }

    private org.meveo.apiv2.billing.InvoiceLine toResourceOrderWithLink(org.meveo.apiv2.billing.InvoiceLine articleMappingLineResource) {
        return ImmutableInvoiceLine.copyOf(articleMappingLineResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(ArticleMappingLineResource.class)
                                .withId(articleMappingLineResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }
}
