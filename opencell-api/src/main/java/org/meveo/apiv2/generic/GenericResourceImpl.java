package org.meveo.apiv2.generic;


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

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import java.util.*;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;

@Stateless
public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;

    @Inject
    private GenericApiAlteringService genericApiAlteringService;

    public static final String URI_INFO = "uriInfo";
    public static final String EXCEPTION_MESSAGE = "message";

    private static Map mapInfoForError = new HashMap();
    public static Map getMapInfoForError() {
        return mapInfoForError;
    }

    @Override
    public Response getAll(String entityName, GenericPagingAndFiltering searchConfig) {
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
        return Response.ok().entity(loadService.findPaginatedRecords(entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth()))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(String entityName, Long id, GenericPagingAndFiltering searchConfig) {
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
        return loadService.findByClassNameAndId(entityClass, id, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth())
                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(finalEntityName, id)).build())
                .orElseThrow(() -> new NotFoundException("entity " + finalEntityName + " with id " + id + " not found"));
    }

    @Override
    public Response getEntity(String entityName, Long id, @Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        mapInfoForError.put( URI_INFO, uriInfo );

        return get(entityName, id,
                GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
    }

    @Override
    public Response getAllEntities(String entityName, @Context UriInfo uriInfo, String inList, @Context HttpHeaders headers )
            throws JsonProcessingException, ParseException {

//        System.out.println( "additionalProperties : " + additionalProperties );
//
//        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
//        System.out.println( "HERE NE getAllEntities 2" );
//        System.out.println( "HERE NE getAllEntities 3" );
//
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) parser.parse(additionalProperties);
//        Iterator<String> keys = jsonObject.keySet().iterator();
//
//        while(keys.hasNext()) {
//            String key = keys.next();
//            if ( key.equals( "offset" ) ) {
//                queryParams.put( "offset", Collections.singletonList( String.valueOf(jsonObject.get( key ))) );
//            }
//            else if ( key.equals( "sort" ) )
//                queryParams.put( "sort", Collections.singletonList( String.valueOf(jsonObject.get( key ))) );
//        }


        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();

        Iterator<String> itQueryParams = queryParams.keySet().iterator();
        System.out.println( "inList DAY NE THANG : " + inList );

        while (itQueryParams.hasNext()){
            String aKey = itQueryParams.next();
            if ( aKey.equals( "inList" ) ) {
                List<String> aList = queryParams.get(aKey);

                String inListString = aList.get(0);
                System.out.println( "inListString DAY NE THANG : " + inListString );
            }
        }

//        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + " and value : ");
//            for ( String str : entry.getValue() )
//                System.out.println("str = " + str );
//        }

        mapInfoForError.put( URI_INFO, uriInfo );

        return getAll(entityName,
                GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams));
    }

    @Override
    public Response getFullListEntities() {
        return Response.ok().entity(GenericOpencellRestful.ENTITIES_MAP).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getRelatedFieldsOfEntity( String entityName ) {
//        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
//        GenericPagingAndFiltering searchConfig =
//                GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParams);
//
//        Set<String> genericFields = null;
//        Set<String> nestedEntities = null;
//        if(searchConfig != null){
//            genericFields = searchConfig.getGenericFields();
//            nestedEntities = searchConfig.getNestedEntities();
//        }
//        Class entityClass = GenericHelper.getEntityClass(entityName);
//        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());

//        return Response.ok().entity( loadService.findRelatedFields(entityClass,
//                genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities,
//                searchConfig.getNestedDepth()) )
//                .links(buildPaginatedResourceLink(entityName)).build();

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
    public Response create(String entityName, String dto, @Context UriInfo uriInfo) {
        entityName = StringUtils.recoverRealName(entityName);
        String finalEntityName = entityName;

        mapInfoForError.put( URI_INFO, uriInfo );
        mapInfoForError.put( EXCEPTION_MESSAGE, "entity " + finalEntityName + " cannot be created" );

        return  genericApiAlteringService.create(finalEntityName, dto)
                .map(entityId -> Response.status(Response.Status.CREATED).entity(Collections.singletonMap("id", entityId))
                .links(buildSingleResourceLink(finalEntityName, entityId))
                .build())
                .orElseThrow(() ->
                        new EJBTransactionRolledbackException("entity " + finalEntityName + " cannot be created."));
    }

    @Override
    public Response delete(String entityName, Long id) {
        entityName = StringUtils.recoverRealName(entityName);
        return Response.status(Response.Status.NO_CONTENT).entity(genericApiAlteringService.delete(entityName, id))
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
