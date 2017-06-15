package org.meveo.api.rest.account;

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
import org.meveo.api.dto.CRMAccountTypeSearchDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.ParentListResponse;
import org.meveo.api.dto.response.account.BusinessAccountModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/businessAccountModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessAccountModelRs extends IBaseRs {
    /**
     * Create a new business account model
     * 
     * @param postData Business account model data
     * @return Request processing status
     */
    @POST
    @Path("/")
    public ActionStatus create(BusinessAccountModelDto postData);

    /**
     * Update an existing business account model
     * 
     * @param postData Business account model data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    public ActionStatus update(BusinessAccountModelDto postData);

    /**
     * Search for a business account model
     * 
     * @param postData Business account model data
     * @return Request processing status
     */
    @GET
    @Path("/")
    public BusinessAccountModelResponseDto find(@QueryParam("businessAccountModelCode") String bamCode);

    /**
     * Remove business account model with a given business account model code.
     * 
     * @param businessAccountModelCode Business account model code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessAccountModelCode}")
    public ActionStatus remove(@PathParam("businessAccountModelCode") String bamCode);

    /**
     * Return meveo's modules
     * 
     * @return
     */
    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    
    /**
     * Install business account module
     * 
     * @param moduleDto The module
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    public ActionStatus install(BusinessAccountModelDto moduleDto);

    /**
     * Find parent entities based on account hierarchy code.
     *
     * @param searchDto
     * @return
     */
    @POST
    @Path("/findParents")
    ParentListResponse findParents(CRMAccountTypeSearchDto searchDto);
}
