package org.meveo.api.rest.codegen;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.IBaseRs;

@Path("/codegen")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface DbModelDocsRs extends IBaseRs {
	 @GET
	 @Path("/dbModelDoc")
	 ActionStatus getDbModelDoc();

}
