package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.BusinessEntityResponseDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;

/**
 * @author Andrius Karpavicius
 **/
@WebService
public interface EntityCustomizationWs extends IBaseWs {

    // Custom entity templates

    @WebMethod
    ActionStatus createEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    @WebMethod
    ActionStatus updateEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    @WebMethod
    CustomEntityTemplateResponseDto findEntityTemplate(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus removeEntityTemplate(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createOrUpdateEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    /**
     * Enable a Custom entity template by its code
     * 
     * @param code Custom entity template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableEntityTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Custom entity template by its code
     * 
     * @param code Custom entity template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableEntityTemplate(@WebParam(name = "code") String code);

    // Custom entity instances

    @WebMethod
    CustomEntityInstanceResponseDto findCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    @WebMethod
    ActionStatus removeCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    @WebMethod
    ActionStatus updateCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    @WebMethod
    ActionStatus createOrUpdateCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    /**
     * Enable a Custom entity instance with a given code
     * 
     * @param cetCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    /**
     * Disable a Custom entity instance with a given code
     * 
     * @param cetCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    // Custom fields

    @WebMethod
    ActionStatus createField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    @WebMethod
    ActionStatus updateField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    @WebMethod
    ActionStatus removeField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    GetCustomFieldTemplateReponseDto findField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    ActionStatus createOrUpdateField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    /**
     * Enable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    /**
     * Disable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    // Entity actions

    @WebMethod
    ActionStatus createAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    @WebMethod
    ActionStatus updateAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    @WebMethod
    ActionStatus removeAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    EntityCustomActionResponseDto findAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    ActionStatus createOrUpdateAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    /**
     * Enable an Entity custom action with a given code
     * 
     * @param actionCode Action code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    /**
     * Disable an Entity custom action with a given code
     * 
     * @param actionCode Action code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    ActionStatus customizeEntity(@WebParam(name = "entityCustomization") EntityCustomizationDto dto);

    @WebMethod
    EntityCustomizationResponseDto findEntityCustomizations(@WebParam(name = "customizedEntityClass") String customizedEntityClass);

    @WebMethod
    BusinessEntityResponseDto listBusinessEntityForCFVByCode(@WebParam(name = "code") String code, @WebParam(name = "wildcode") String wildcode);

    @WebMethod
    CustomEntityTemplatesResponseDto listEntityTemplates(@WebParam(name = "customEntityTemplateCode") String customEntityTemplateCode);

    @WebMethod
    EntityCustomizationResponseDto listELFiltered(@WebParam(name = "appliesTo") String appliesTo, @WebParam(name = "entityCode") String entityCode);

    @WebMethod
    ActionStatus executeAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo, @WebParam(name = "entityCode") String entityCode);

}