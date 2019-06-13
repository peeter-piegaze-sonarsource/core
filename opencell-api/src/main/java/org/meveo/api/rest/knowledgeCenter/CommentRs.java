package org.meveo.api.rest.knowledgeCenter;

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
import org.meveo.api.dto.knowledgeCenter.CommentDto;
import org.meveo.api.dto.response.knowledgeCenter.GetCommentResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/knowledgecenter/comment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CommentRs extends IBaseRs {
	@POST
    @Path("/")
    ActionStatus create(CommentDto postData);
	
	@PUT
    @Path("/")
    ActionStatus update(CommentDto postData);
	
	@POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CommentDto postData);
	
	@GET
    @Path("/")
    GetCommentResponseDto find(@QueryParam("code") String code);
	
	@DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);
}
