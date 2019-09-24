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
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.response.GetTradingLanguageResponse;
import org.meveo.api.dto.response.LanguagesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language} and {@link org.meveo.model.billing.TradingLanguage}.
 * 
 * @author Edward P. Legaspi
 * 
 **/
@Path("/language")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface LanguageRs extends IBaseRs {

    /**
     * Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.
     * 
     * @param postData language to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(LanguageDto postData);

    /**
     * Search language given a code.
     * 
     * @param languageCode language's code
     * @return language
     */
    @GET
    @Path("/")
    GetTradingLanguageResponse find(@QueryParam("languageCode") String languageCode);

    /**
     * Does not delete a language but the tradingLanguage associated to it.
     * 
     * @param languageCode language's code
     * @return action satus
     */
    @DELETE
    @Path("/{languageCode}")
    ActionStatus remove(@PathParam("languageCode") String languageCode);

    /**
     * modify a language. Same input parameter as create. The language and trading Language are created if they don't exists. The operation fails if the tradingLanguage is null.
     * 
     * @param postData language to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(LanguageDto postData);

    /**
     * Create or update a language if it doesn't exists.
     * 
     * @param postData language to be created or updated
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(LanguageDto postData);

    /**
     * Enable a Trading language with a given language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Trading language with a given language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * List contents matching a given criteria
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
    public LanguagesResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

}