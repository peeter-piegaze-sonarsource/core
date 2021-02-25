package org.meveo.apiv2.generic;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.meveo.api.exception.NotPluralFormException;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.exception.NotPluralFormMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.util.Inflector;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.Set;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;

@Stateless
public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;

    @Inject
    private GenericApiAlteringService genericApiAlteringService;

    public static final String URI_INFO = "uriInfo";
    public static final String EXCEPTION_MESSAGE = "message";

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders httpHeaders;

    @Override
    public Response getAll(Boolean extractList, String entityName, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());

        return Response.ok().entity(loadService.findPaginatedRecords(extractList, entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth()))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(Boolean extractList, String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());

        return loadService.findByClassNameAndId(extractList, entityClass, id, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth())
                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(entityName , id)).build())
                .orElseThrow(() -> new NotFoundException("entity " + entityName + " with id "+id+ " not found."));
    }

    @Override
    public Response getEntity(Boolean extractList, String entityName, Long id) throws JsonProcessingException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // if entityName is of plural form, process the request
        if ( Inflector.getInstance().pluralize(entityName).equals(entityName) ) {
            entityName = Inflector.getInstance().singularize(entityName);

            ValidationUtils.checkEntityExistence(entityName);
            ValidationUtils.checkCamelCaseFormat(entityName);

            return get(extractList, entityName, id,
                    GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
        }
        // otherwise, entityName is not of plural form, raise an exception
        else {
            NotPluralFormException notPluralFormException =
                    new NotPluralFormException("The entity name " + entityName + " is not a valid plural form");
            NotPluralFormMapper notPluralFormMapper = new NotPluralFormMapper();
            return notPluralFormMapper.toResponse(notPluralFormException);
        }
    }

    @Override
    public Response getAllEntities(Boolean extractList, String entityName)
            throws JsonProcessingException, ParseException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        MultivaluedMap<String, String> requestHeaders = httpHeaders.getRequestHeaders();

        // if entityName is of plural form, process the request
        if ( Inflector.getInstance().pluralize(entityName).equals(entityName) ) {
            entityName = Inflector.getInstance().singularize(entityName);

            ValidationUtils.checkEntityExistence(entityName);
            ValidationUtils.checkCamelCaseFormat(entityName);

            return getAll(extractList, entityName,
                    GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
        }
        // otherwise, entityName is not of plural form, raise an exception
        else {
            NotPluralFormException notPluralFormException =
                    new NotPluralFormException("The entity name " + entityName + " is not a valid plural form");
            NotPluralFormMapper notPluralFormMapper = new NotPluralFormMapper();
            return notPluralFormMapper.toResponse(notPluralFormException);
        }
    }

    @Override
    public Response getFullListEntities() {
        return Response.ok().entity(GenericOpencellRestful.ENTITIES_MAP).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getRelatedFieldsAndTypesOfEntity( String entityName ) {
        Class entityClass = GenericHelper.getEntityClass(entityName);
        return Response.ok().entity(getPersistenceService(entityClass).mapRelatedFields()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response head(String entityName, Long id) {
        entityName = Inflector.getInstance().singularize(entityName);
        Class entityClass = GenericHelper.getEntityClass(entityName);
        String finalEntityName = entityName;
        return loadService.findByClassNameAndId(entityClass, id)
                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(finalEntityName, id)).build())
                .orElseThrow(() -> new NotFoundException("entity " + finalEntityName + " with id " + id + " not found"));
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        entityName = Inflector.getInstance().singularize(entityName);
        return Response.ok().entity(genericApiAlteringService.update(entityName, id, dto))
                .links(buildSingleResourceLink(entityName, id))
                .build();
    }
    
    @Override
    public Response create(String entityName, String dto) {
        entityName = Inflector.getInstance().singularize(entityName);
        String finalEntityName = entityName;

        return  genericApiAlteringService.create(finalEntityName, dto)
                .map(entityId -> Response.status(Response.Status.CREATED).entity(Collections.singletonMap("id", entityId))
                .links(buildSingleResourceLink(finalEntityName, entityId))
                .build())
                .get();
    }

    @Override
    public Response delete(String entityName, Long id) {
        entityName = Inflector.getInstance().singularize(entityName);
        return Response.ok().entity(genericApiAlteringService.delete(entityName, id))
                        .links(buildSingleResourceLink(entityName, id)).build();
    }

    @Override
    public Response getVersions() {
        return Response.ok().entity(GenericOpencellRestful.VERSION_INFO).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private Link buildPaginatedResourceLink(String entityName) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction()
                .withDeleteAction().build(entityName);
    }
    private Link buildSingleResourceLink(String entityName, Long entityId) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction().withId(entityId)
                .withDeleteAction().build(entityName);
    }
}
