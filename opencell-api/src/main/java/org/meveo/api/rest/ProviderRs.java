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
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.ProvidersDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;

/**
 * Web service for managing Provider.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/provider")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ProviderRs extends IBaseRs {

    /**
     * Create provider. Deprecated in v. 4.5. Use updateProvider() instead.
     * 
     * @param postData Provider data to be created
     * @return action status
     */
    @Deprecated
    @POST
    @Path("/")
    ActionStatus create(ProviderDto postData);

    /**
     * Retrieve provider information.
     * 
     * @return Provider information
     */
    @GET
    @Path("/")
    GetProviderResponse find();

    /**
     * Update provider.
     * 
     * @param postData Provider data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(ProviderDto postData);

    /**
     * Returns list of trading countries, currencies and languages.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return trading configuration.
     */
    @GET
    @Path("/getTradingConfiguration")
    GetTradingConfigurationResponseDto findTradingConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return invoicing configuration
     */
    @GET
    @Path("/getInvoicingConfiguration")
    GetInvoicingConfigurationResponseDto findInvoicingConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of customer brands, categories and titles.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return customer configuration
     */
    @GET
    @Path("/getCustomerConfiguration")
    GetCustomerConfigurationResponseDto findCustomerConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of payment method and credit categories.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return customer account configuration
     */
    @GET
    @Path("/getCustomerAccountConfiguration")
    GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Create or update a provider if it doesn't exists. Deprecated in v. 4.5. Use updateProvider() instead.
     * 
     * @param postData Provider data
     * @return action status
     */
    @Deprecated
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ProviderDto postData);

    /**
     * @param postData provider to be updated
     * @return action status
     */
    @PUT
    @Path("/updateProviderCF")
    ActionStatus updateProviderCF(ProviderDto postData);

    /**
     * @param providerCode provider's code
     * @return provider if exists
     */
    @GET
    @Path("/findProviderCF")
    GetProviderResponse findProviderCF(@QueryParam("providerCode") String providerCode);

    /**
     * Register a new tenant
     * 
     * @param postData Tenant/Provider data
     * @return Action status
     */
    @POST
    @Path("/createTenant")
    public ActionStatus createTenant(ProviderDto postData);

    /**
     * List tenants
     * 
     * @return A list of Tenant/provider data
     */
    @GET
    @Path("/listTenants")
    public ProvidersDto listTenants();

    /**
     * Remove a tenant
     * 
     * @param providerCode Tenant/provider code
     * @return Action status
     */
    @DELETE
    @Path("/{providerCode}")
    public ActionStatus removeTenant(@PathParam("providerCode") String providerCode);
}
