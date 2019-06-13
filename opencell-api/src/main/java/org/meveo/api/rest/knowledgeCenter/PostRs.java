package org.meveo.api.rest.knowledgeCenter;

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
import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.knowledgeCenter.GetPostResponseDto;
import org.meveo.api.dto.response.knowledgeCenter.PostsResponseDto;
import org.meveo.api.rest.IBaseRs;


@Path("/knowledgecenter/post")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface PostRs extends IBaseRs{
	@POST
    @Path("/")
    ActionStatus create(PostDto postData);

	@PUT
    @Path("/")
    ActionStatus update(PostDto postData);
	
	@POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(PostDto postData);
	
	@GET
    @Path("/")
    GetPostResponseDto find(@QueryParam("code") String code);
    
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);
    
    /**
     * List posts matching a given criteria
     *
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of contacts
     */
    @GET
    @Path("/list")
    public PostsResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

}
