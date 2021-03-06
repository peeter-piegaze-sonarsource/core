/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Path("/occTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OccTemplateRs extends IBaseRs {

    /**
     * Create OccTemplate.
     * 
     * @param postData posted data to API (account operation template)
     * @return action status.
     */
    @POST
    @Path("/")
    ActionStatus create(OccTemplateDto postData);

    /**
     * Update OccTemplate.
     * 
     * @param postData posted data to API
     * @return action status.
     */
    @PUT
    @Path("/")
    ActionStatus update(OccTemplateDto postData);

    /**
     * Search OccTemplate with a given code.
     * 
     * @param occtemplateCode  code of account operation template
     * @return account operation template
     */
    @GET
    @Path("/")
    GetOccTemplateResponseDto find(@QueryParam("occTemplateCode") String occtemplateCode);

    /**
     * Remove OccTemplate with a given code.
     * 
     * @param occTemplateCode code of account operation template
     * @return action status.
     */
    @DELETE
    @Path("/{occTemplateCode}")
    ActionStatus remove(@PathParam("occTemplateCode") String occTemplateCode);

    /**
     * Create or update OccTemplate.
     * 
     * @param postData posted data
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(OccTemplateDto postData);
  
    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of account operations
     */
    @GET
    @Path("/list")
    public GetOccTemplatesResponseDto listGet(@QueryParam("query") String query,
            @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("accountCode") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of account operations
     */
    @POST
    @Path("/list")
    public GetOccTemplatesResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
