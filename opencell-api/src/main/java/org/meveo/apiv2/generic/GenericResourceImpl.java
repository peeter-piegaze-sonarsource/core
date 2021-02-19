package org.meveo.apiv2.generic;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.commons.utils.StringUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;

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

    private static Map mapInfoForError = new HashMap();
    public static Map getMapInfoForError() {
        return mapInfoForError;
    }

    @Override
    public Response getAll(Boolean extractList, String entityName, GenericPagingAndFiltering searchConfig) {
System.out.println( "searchConfig in getAll HERE THANG NGUYEN : " + searchConfig );
        entityName = StringUtils.recoverRealName(entityName);
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());

        mapInfoForError.put( URI_INFO, uriInfo );
        mapInfoForError.put( EXCEPTION_MESSAGE, "entities " + entityName + " cannot be retrieved" );

        return Response.ok().entity(loadService.findPaginatedRecords(extractList, entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth()))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(Boolean extractList, String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        entityName = StringUtils.recoverRealName(entityName);
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        String finalEntityName = entityName;
        mapInfoForError.put( URI_INFO, uriInfo );

        return loadService.findByClassNameAndId(extractList, entityClass, id, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth())
                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(entityName, id)).build())
                .orElseThrow(() -> new NotFoundException("entity " + finalEntityName + " with id "+id+ " not found."));
    }

    @Override
    public Response getEntity(String entityName, Long id) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        return get(entityName, id,
                GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
    }

    @Override
    public Response getAllEntities(String entityName )
            throws JsonProcessingException, ParseException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        MultivaluedMap<String, String> requestHeaders = httpHeaders.getRequestHeaders();

        return getAll(entityName,
                GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
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
        entityName = StringUtils.recoverRealName(entityName);
        Class entityClass = GenericHelper.getEntityClass(entityName);
        String finalEntityName = entityName;
        return loadService.findByClassNameAndId(entityClass, id)
                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(finalEntityName, id)).build())
                .orElseThrow(() -> new NotFoundException("entity " + finalEntityName + " with id " + id + " not found"));
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        entityName = StringUtils.recoverRealName(entityName);
        return Response.status(Response.Status.NO_CONTENT).entity(genericApiAlteringService.update(entityName, id, dto))
                .links(buildSingleResourceLink(entityName, id))
                .build();
    }
    
    @Override
    public Response create(String entityName, String dto) {
        entityName = StringUtils.recoverRealName(entityName);
        String finalEntityName = entityName;

        mapInfoForError.put( URI_INFO, uriInfo );
        mapInfoForError.put( EXCEPTION_MESSAGE, "entity " + finalEntityName + " cannot be created" );

        return  genericApiAlteringService.create(finalEntityName, dto)
                .map(entityId -> Response.status(Response.Status.CREATED).entity(Collections.singletonMap("id", entityId))
                .links(buildSingleResourceLink(finalEntityName, entityId))
                .build())
                .get();
    }

    @Override
    public Response delete(String entityName, Long id) {
        entityName = StringUtils.recoverRealName(entityName);
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
