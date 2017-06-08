package org.meveo.api;

import java.util.Date;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.IEntity;

/**
 * An interface of CRUD API service class for entities that are versioned - that contain validity dates
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public interface ApiVersionedService<E extends IEntity, T extends BaseDto> {

    /**
     * Find entity identified by code
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * 
     * @return A DTO of entity
     * @throws EntityDoesNotExistsException Entity was not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    public T find(String code, Date validFrom, Date validTo) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Find entity identified by code. Return null if not found
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * 
     * @return A DTO of entity or NULL if not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    public T findIgnoreNotFound(String code, Date validFrom, Date validTo) throws MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Create or update an entity from DTO
     * 
     * @param dtoData DTO data
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public E createOrUpdate(T dtoData) throws MeveoApiException, BusinessException;

}
