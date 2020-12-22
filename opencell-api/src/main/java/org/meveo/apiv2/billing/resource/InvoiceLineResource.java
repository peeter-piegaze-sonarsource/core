package org.meveo.apiv2.billing.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.AccountingArticle;
import org.meveo.apiv2.billing.InvoiceLine;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/invoiceLine")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface InvoiceLineResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an invoice line resource",
            tags = { "InvoiceLine" },
            description ="create new invoice line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the invoice line successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when invoice line information contains an error")
            })
    Response createInvoiceLine(@Parameter(description = "the accounting article object", required = true) InvoiceLine invoiceLine);

}