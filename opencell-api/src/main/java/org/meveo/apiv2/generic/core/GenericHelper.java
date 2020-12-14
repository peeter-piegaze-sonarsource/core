package org.meveo.apiv2.generic.core;

import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.catalog.OfferServiceTemplate;

import javax.persistence.Entity;
import java.util.Map;
import java.util.stream.Collectors;

import static org.meveo.apiv2.generic.ValidationUtils.*;


public class GenericHelper {
    public final static Map<String, Class> entitiesByName;

    /*
     * Initialize entitiesByName map
     */
    static {
        entitiesByName = populateEntitiesToHandleByGenericApi();
    }

    /**
     * get all classes that should be handled by Generic API
     *
     * @return map of entities classes with their simple names as keys
     */
    private static Map<String, Class> populateEntitiesToHandleByGenericApi() {
        Map<String, Class> entitiesByName  = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName(), clazz -> clazz));
        populateNonBaseEntityClass(entitiesByName);
        return entitiesByName;
    }

    /**
     * get manually classes that should be handled by Generic API but which are not extending
     * {@link org.meveo.model.BaseEntity}
     * @param entitiesByName
     */
    private static void populateNonBaseEntityClass(Map<String, Class> entitiesByName) {
        entitiesByName.put(OfferServiceTemplate.class.getSimpleName(), OfferServiceTemplate.class);
    }

    /**
     * Get an entity class by its simple name
     * @param entityName entity simple name
     * @return entity class
     */
    public static Class getEntityClass(String entityName) {
        checkEntityName(entityName);
        checkEntityFormat(entityName);
        entityName = Character.toUpperCase(entityName.charAt(0)) + entityName.substring(1);
        Class entityClass = entitiesByName.get(entityName);
        checkEntityClass(entityClass);
        return entityClass;
    }

    public static Long getDefaultLimit() {
        return GenericOpencellRestful.API_LIST_DEFAULT_LIMIT;
    }
}