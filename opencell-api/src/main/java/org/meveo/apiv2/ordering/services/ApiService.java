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

package org.meveo.apiv2.ordering.services;

import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.service.base.PersistenceService;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

public interface ApiService<T extends BaseEntity> {
    List<T> list(Long offset, Long limit, String sort, String orderBy, String filter);
    Long getCount(String filter);
    Optional<T> findById(Long id);
    T create(T baseEntity);
    Optional<T> update(Long id, T baseEntity);
    Optional<T> patch(Long id, T baseEntity);
    Optional<T> delete(Long id);

    default <T extends IEntity> T loadEntityById(PersistenceService<T> service, Long id, String entityName){
        T baseEntity = service.findById(id);
        if(baseEntity == null)
            throw new BadRequestException("No " + entityName + " found with id: " + id);
        return baseEntity;
    }
}
