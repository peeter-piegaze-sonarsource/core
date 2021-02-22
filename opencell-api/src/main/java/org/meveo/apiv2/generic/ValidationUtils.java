package org.meveo.apiv2.generic;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.NotCamelCaseException;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.commons.utils.StringUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidationUtils {
    private static ValidationUtils INSTANCE = new ValidationUtils();

    public static ValidationUtils checkEntityName(String entityName) {
        return check(entityName, StringUtils::isBlank, () -> new NotFoundException("The entityName should not be null or empty"));
    }

    public static ValidationUtils checkCamelCaseFormat(String entityName) {
        return check(entityName, GenericHelper.listCamelCaseName, StringUtils::isNotWellFormedCamelCase,
                () -> new NotCamelCaseException("All the letters of entityName " + entityName
                        + " should be in lowercase, except for the first letters in each word in a compound word"));
    }

    public static ValidationUtils checkEntityExistence(String entityName) {
        return check(entityName, GenericHelper.listCamelCaseName, StringUtils::isNotExistingEntity,
                () -> new NotFoundException("The entity " + entityName + " does not exist"));
    }

    public static ValidationUtils checkISOFormats(Map<String, Object> readValueMap) {
        StringBuffer fieldName = new StringBuffer();
        return check(readValueMap, fieldName, StringUtils::isNotISOFormats,
                () -> new NotFoundException("The entity cannot be created because the field " +
                        fieldName + " does not comply with ISO format"));
    }
    
    public static ValidationUtils checkId(Long id) {
        return check(id, Objects::isNull, () -> new InvalidParameterException("The requested id should not be null"));
    }
    
    public static ValidationUtils checkDto(String dto) {
        return check(dto, StringUtils::isBlank, () -> new InvalidParameterException("The given json dto representation should not be null or empty"));
    }
    
    public static ValidationUtils checkEntityClass(Class entityClass) {
        return check(entityClass, Objects::isNull, () -> new NotFoundException("The requested entity does not exist"));
    }
    
    public static <T> List<T> checkRecords(List<T> records, String className) {
        check(records, Objects::isNull, () -> new NotFoundException(String.format("Unable to find records fo type %s", className)));
        return records;
    }
    
    public static <T> T checkRecord(T record, String className, Long id) {
        check(record, Objects::isNull, () -> new NotFoundException(String.format("%s with code=%s does not exists.", className, id.toString())));
        return record;
    }

    private static <T> ValidationUtils check(T object, Predicate<T> condition, Supplier<? extends RuntimeException> ex) {
        if (condition.test(object)) {
            throw ex.get();
        }
        return INSTANCE;
    }

    private static <T,S> ValidationUtils check(T object1, S object2, BiPredicate<T, S> condition, Supplier<? extends RuntimeException> ex) {
        if (condition.test(object1, object2)) {
            throw ex.get();
        }
        return INSTANCE;
    }
}
