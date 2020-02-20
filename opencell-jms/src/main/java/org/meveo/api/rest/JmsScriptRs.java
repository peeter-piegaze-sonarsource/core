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
import org.meveo.api.dto.jms.JmsScriptDto;
import org.meveo.api.dto.jms.JmsScriptListResponseDto;
import org.meveo.api.dto.jms.JmsScriptResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Rest Service for JmsScript
 * 
 * @author Axione
 */
@Path("/jmsScript")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface JmsScriptRs extends IBaseRs {

	/**
     * Create a new jms script 
     * 
     * @param postData The jms script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    JmsScriptResponseDto create(JmsScriptDto postData);
    
    /**
     * Update an existing script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    JmsScriptResponseDto update(JmsScriptDto postData);

    /**
     * Remove an existing script instance with a given code .
     * 
     * @param scriptInstanceCode The script instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{jmsScriptCode}")
    ActionStatus remove(@PathParam("jmsScriptCode") String scriptInstanceCode);

    /**
     * Find a script instance with a given code.
     *
     * @param scriptInstanceCode The script instance's code
     * @return script instance
     */
    @GET
    @Path("/")
    JmsScriptResponseDto find(@QueryParam("jmsScriptCode") String scriptInstanceCode);
    
    /**
     * Create new or update an existing script instance with a given code.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    JmsScriptResponseDto createOrUpdate(JmsScriptDto postData);
    
    /**
     * Enable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Get list of jms script
     * 
     * @return List of jms
     */
    @GET
    @Path("/list")
    JmsScriptListResponseDto list();


}
