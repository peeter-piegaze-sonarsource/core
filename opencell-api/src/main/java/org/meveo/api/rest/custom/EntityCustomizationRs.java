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

package org.meveo.api.rest.custom;

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
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.BusinessEntityResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Path("/entityCustomization")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface EntityCustomizationRs extends IBaseRs {

    /**
     * Define a new custom entity template including fields and applicable actions
     * 
     * @param dto The custom entity instance's data
     * @return Request processing status
     */
    @POST
    @Path("/entity/")
    ActionStatus createEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Update custom entity template definition
     * 
     * @param dto The custom entity instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/entity/")
    ActionStatus updateEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Remove custom entity template definition given its code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/entity/{customEntityTemplateCode}")
    ActionStatus removeEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Get custom entity template definition including its fields and applicable actions
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @return instance of CustomEntityTemplateResponseDto
     */
    @GET
    @Path("/entity/{customEntityTemplateCode}")
    CustomEntityTemplateResponseDto findEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * List custom entity templates.
     * 
     * @param customEntityTemplateCode An optional and partial custom entity template code
     * @return instance of CustomEntityTemplatesResponseDto
     */
    @GET
    @Path("/entity/list")
    CustomEntityTemplatesResponseDto listEntityTemplates(@QueryParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Define new or update existing custom entity template definition
     * 
     * @param dto The custom entity instance's data
     * @return Request processing status
     */
    @POST
    @Path("/entity/createOrUpdate")
    ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * To be sure the compatibility of above method we will create a new one. Define new or update existing custom entity template definition
     * 
     * @param dto The custom entity instance's data
     * @return Request processing status
     */
    @POST
    @Path("/cet/createOrUpdate")
    ActionStatus createOrUpdateCustumizedEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Enable a Custom entity template with a given code
     * 
     * @param code Custom entity template code
     * @return Request processing status
     */
    @POST
    @Path("/entity/{code}/enable")
    ActionStatus enableEntityTemplate(@PathParam("code") String code);

    /**
     * Disable a Custom entity template with a given code
     * 
     * @param code Custom entity template code
     * @return Request processing status
     */
    @POST
    @Path("/entity/{code}/disable")
    ActionStatus disableEntityTemplate(@PathParam("code") String code);

    /**
     * Customize a standard Meveo entity definition by adding fields and/or custom actions
     * 
     * @param dto The custom entity instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/customize/")
    ActionStatus customizeEntity(EntityCustomizationDto dto);

    /**
     * Get customizations made on a standard Meveo entity given its class
     * 
     * @param customizedEntityClass Standard Meveo entity class name
     * @return instance of EntityCustomizationResponseDto
     */
    @GET
    @Path("/customize/{customizedEntityClass}")
    EntityCustomizationResponseDto findEntityCustomizations(@PathParam("customizedEntityClass") String customizedEntityClass);

    /**
     * Define a new custom field
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/field/")
    ActionStatus createField(CustomFieldTemplateDto postData);

    /**
     * Update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @PUT
    @Path("/field/")
    ActionStatus updateField(CustomFieldTemplateDto postData);

    /**
     * Remove custom field definition given its code and entity it applies to
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity custom field applies to
     * @return Request processing status
     */
    @DELETE
    @Path("/field/{customFieldTemplateCode}/{appliesTo}")
    ActionStatus removeField(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get custom field definition
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity custom field applies to
     * @return instance of GetCustomFieldTemplateReponseDto.
     */
    @GET
    @Path("/field/")
    GetCustomFieldTemplateReponseDto findField(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/field/createOrUpdate")
    ActionStatus createOrUpdateField(CustomFieldTemplateDto postData);

    /**
     * Enable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/field/{customFieldTemplateCode}/{appliesTo}/enable")
    ActionStatus enableField(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Disable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/field/{customFieldTemplateCode}/{appliesTo}/disable")
    ActionStatus disableField(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Define a new entity action
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/action/")
    ActionStatus createAction(EntityCustomActionDto postData);

    /**
     * Update existing entity action definition
     * 
     * @param dto posted data to API
     * @return Request processing status
     */
    @PUT
    @Path("/action/")
    ActionStatus updateAction(EntityCustomActionDto dto);

    /**
     * Remove entity action definition given its code and entity it applies to
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return Request processing status
     */
    @DELETE
    @Path("/action/{actionCode}/{appliesTo}")
    ActionStatus removeAction(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get entity action definition
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return instance of EntityCustomActionResponseDto
     */
    @GET
    @Path("/action/")
    EntityCustomActionResponseDto findAction(@QueryParam("actionCode") String actionCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing entity action definition
     * 
     * @param dto posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/action/createOrUpdate")
    ActionStatus createOrUpdateAction(EntityCustomActionDto dto);

    /**
     * Enable a Entity action with a given code
     * 
     * @param actionCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/field/{actionCode}/{appliesTo}/enable")
    ActionStatus enableAction(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Disable a Entity action with a given code
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/field/{actionCode}/{appliesTo}/disable")
    ActionStatus disableAction(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Returns a List of BusinessEntities given a CustomFieldTemplate code. The CustomFieldTemplate is pulled from the database and entityClass is use in query. For example entity
     * class is of type OfferTemplate, then it will return a list of OfferTemplates.
     * 
     * @param code CFT code
     * @param wildcode code filter
     * @return instance of BusinessEntityResponseDto
     */
    @GET
    @Path("/listBusinessEntityForCFVByCode/")
    BusinessEntityResponseDto listBusinessEntityForCFVByCode(@QueryParam("code") String code, @QueryParam("wildcode") String wildcode);

    /**
     * Returns a list of filtered CustomFieldTemplate of an entity. The list of entity is evaluted againsts the entity with the given code.
     * 
     * @param appliesTo - the type of entity to which the CFT applies. eg OfferTemplate, ServiceTemplate.
     * @param entityCode - code of the entity
     * @param entityId - id of the entity
     * @return instance of EntityCustomizationResponseDto
     */
    @GET
    @Path("/entity/listELFiltered")
    EntityCustomizationResponseDto listELFiltered(@QueryParam("appliesTo") String appliesTo, @QueryParam("entityCode") String entityCode, @QueryParam("entityId") Long entityId);

    /**
     * Execute and action of a given entity
     * 
     * @param actionCode Action code
     * @param appliesTo Entity it applies to
     * @param entityCode Entity code to execute action on
     * @return Request processing status
     */
    @POST
    @Path("/entity/action/execute/{actionCode}/{appliesTo}/{entityCode}")
    ActionStatus execute(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo, @PathParam("entityCode") String entityCode);

}