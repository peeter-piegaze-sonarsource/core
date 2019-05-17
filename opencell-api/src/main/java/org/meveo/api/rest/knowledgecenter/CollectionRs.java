package org.meveo.api.rest.knowledgecenter;

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
import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.dto.response.knowledgecenter.GetCollectionResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/knowledgecenter/collection")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CollectionRs extends IBaseRs{
	@POST
    @Path("/")
    ActionStatus create(CollectionDto postData);
	
	@PUT
    @Path("/")
    ActionStatus update(CollectionDto postData);
	
	@GET
    @Path("/")
    GetCollectionResponseDto find(@QueryParam("code") String code);
    
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);
}
