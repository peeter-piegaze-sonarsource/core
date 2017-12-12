package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.response.GetRoleResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

@Path("/role")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RoleRs extends IBaseRs {

    /**
     * Create role.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    public ActionStatus create(RoleDto postData);

    /**
     * Update role.
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    public ActionStatus update(RoleDto postData);

    /**
     * Remove role.
     * 
     * @param rolename Role name
     * @return
     */
    @DELETE
    @Path("/{roleName}/{provider}")
    public ActionStatus remove(@PathParam("roleName") String roleName);

    /**
     * Search role.
     * 
     * @param rolename Role name
     * @return
     */
    @GET
    @Path("/")
    public GetRoleResponse find(@QueryParam("roleName") String roleName);

    /**
     * Create or update role
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(RoleDto postData);

    /**
     * List roles matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of roles
     */
    @GET
    @Path("/list")
    public RolesDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("name") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List roles matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @return A list of roles
     */
    @POST
    @Path("/list")
    public RolesDto listPost(PagingAndFiltering pagingAndFiltering);
    
    /**
     * List external roles.
     * @return
     */
    @GET
    @Path("/external")
    public RolesDto listExternalRoles();

}