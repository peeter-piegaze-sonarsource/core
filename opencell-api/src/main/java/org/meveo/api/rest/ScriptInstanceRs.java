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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 * **/
@Path("/scriptInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ScriptInstanceRs extends IBaseRs {

    /**
     * Create a new script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ScriptInstanceReponseDto create(ScriptInstanceDto postData);

    /**
     * Update an existing script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ScriptInstanceReponseDto update(ScriptInstanceDto postData);

    /**
     * Remove an existing script instance with a given code .
     * 
     * @param scriptInstanceCode The script instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{scriptInstanceCode}")
    ActionStatus remove(@PathParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Find a script instance with a given code.
     *
     * @param scriptInstanceCode The script instance's code
     * @return script instance
     */
    @GET
    @Path("/")
    GetScriptInstanceResponseDto find(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Execute a script instance with a given code and list of parameters for the context of the script
     *
     * @param scriptInstanceCode The script instance's code
     * @return response of the script
     */
    @GET
    @Path("/execute")
    Response execute(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Intercept data from http post
     *
     * @param formParams
     * @return response of the script
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("/received/receivedPOST1")
    public Response receivedPOST1(MultivaluedMap<String, String> formParams);

    /**
     * Intercept data from http post
     *
     * @return response of the script
     */
    @POST
    @Path("/received/receivedPOST2")
    public Response receivedPOST2();

    /**
     * Intercept data from http post
     *
     * @return response of the script
     */
    @GET
    @Path("/received/receivedGET")
    public Response receivedGET();


    /**
     * Create new or update an existing script instance with a given code.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData);

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

}