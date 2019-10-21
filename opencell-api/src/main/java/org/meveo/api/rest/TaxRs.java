package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.Tax}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/tax")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TaxRs extends IBaseRs {

    /**
     * Create tax. Description per language can be defined
     * 
     * @param postData tax to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(TaxDto postData);

    /**
     * Update tax. Description per language can be defined
     * 
     * @param postData tax to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(TaxDto postData);

    /**
     * Search tax with a given code.
     * 
     * @param taxCode tax's
     * @return tax if exists
     */
    @GET
    @Path("/")
    GetTaxResponse find(@QueryParam("taxCode") String taxCode);

    /**
     * Remove tax with a given code.
     * 
     * @param taxCode tax's code
     * @return action status
     */
    @DELETE
    @Path("/{taxCode}")
    ActionStatus remove(@PathParam("taxCode") String taxCode);

    /**
     * Create or uptadate a tax. 
     *
     * @param postData tax to be created or updated
     * @return action status
     */
    @POST 
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TaxDto postData);

    /**
     * Search for the list of taxes.
     *
     * @return list of all taxes.
     */
    @GET 
    @Path("/list")
    GetTaxesResponse list();
}
