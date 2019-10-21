package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;

/**
 * @author Tyshan Shi
 * 
 **/
@Path("/filter")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface FilterRs extends IBaseRs {

    /**
     * Create new or update an existing filter with a given code
     * 
     * @param postData The filter's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(FilterDto postData);

    /**
     * Find a filter with a given code
     *
     * @param filterCode The job instance's code
     * @return Dto for FilteredList API
     */
    @GET
    @Path("/")
    public GetFilterResponseDto find(@QueryParam("filterCode") String filterCode);

    /**
     * Enable a Filter with a given code
     * 
     * @param code Filter code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Filter with a given code
     * 
     * @param code Filter code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}
