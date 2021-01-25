package org.meveo.apiv2.generic.core;

import org.meveo.api.dto.IEntityDto;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.meveo.apiv2.generic.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.generic.ValidationUtils.checkEntityName;


public class GenericHelper {
    public final static Map<String, Class> entitiesByName;
    public final static Map<String, Class> entitiesDtoByName;

    /*
     * Initialize entitiesByName map
     */
    static {
        entitiesByName = populateEntitiesToHandleByGenericApi();
        entitiesDtoByName = populateEntitiesDtoToHandleByGenericApi();
    }

    /**
     * get all classes that should be handled by Generic API
     *
     * @return map of entities classes with their simple names as keys
     */
    private static Map<String, Class> populateEntitiesToHandleByGenericApi() {
        Map<String, Class> entitiesByName  = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
        populateNonBaseEntityClass(entitiesByName);
        return entitiesByName;
    }

    /**
     * get all dto classes that should be handled by Generic API
     *
     * @return map of entities dto classes with their simple names as keys
     */
    private static Map<String, Class> populateEntitiesDtoToHandleByGenericApi() {
        Reflections reflections = new Reflections("org.meveo.api.dto");
        Map<String, Class> entitiesDtoByName = new HashMap<>();
        for ( Class aClass : reflections.getSubTypesOf(IEntityDto.class) ) {
            if ( aClass.getSimpleName().equals("PricePlanMatrixDto") )
                entitiesDtoByName.put( "priceplandto", aClass );
            else
                entitiesDtoByName.put( aClass.getSimpleName().toLowerCase(), aClass );
        }
        return entitiesDtoByName;
    }

    /**
     * get manually classes that should be handled by Generic API but which are not extending
     * {@link org.meveo.model.BaseEntity}
     * @param entitiesByName
     */
    private static void populateNonBaseEntityClass(Map<String, Class> entitiesByName) {
        entitiesByName.put(OfferServiceTemplate.class.getSimpleName().toLowerCase(), OfferServiceTemplate.class);
    }

    /**
     * Get an entity class by its simple name
     * @param entityName entity simple name
     * @return entity class
     */
    public static Class getEntityClass(String entityName) {
        checkEntityName(entityName);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        return entityClass;
    }

    /**
     * Get an entity class by its simple name
     * @param entityDtoName entity simple name
     * @return entity class
     */
    public static Class getEntityDtoClass(String entityDtoName) {
        checkEntityName(entityDtoName);
        Class entityDtoClass = entitiesDtoByName.get(entityDtoName);
        return entityDtoClass;
    }

    public static Long getDefaultLimit() {
        return GenericOpencellRestful.API_LIST_DEFAULT_LIMIT;
    }
}