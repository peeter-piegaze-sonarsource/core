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

package org.meveo.api.rest.tax.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxMappingListResponseDto;
import org.meveo.api.dto.response.tax.TaxMappingResponseDto;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tax.TaxMappingRs;
import org.meveo.api.tax.TaxMappingApi;

/**
 * REST interface definition of Tax mapping API
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxMappingRsImpl extends BaseRs implements TaxMappingRs {

    @Inject
    private TaxMappingApi apiService;

    @Override
    public ActionStatus create(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.create(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxMappingResponseDto find(String code) {
        TaxMappingResponseDto result = new TaxMappingResponseDto();

        try {
            Long id = Long.parseLong(code);
			result.setDto(apiService.find(id));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.update(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	Long id = Long.parseLong(code);
            apiService.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxMappingListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        TaxMappingListResponseDto result;

        try {
            result = new TaxMappingListResponseDto(apiService.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new TaxMappingListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxMappingListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        TaxMappingListResponseDto result;

        try {
            result = new TaxMappingListResponseDto(apiService.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new TaxMappingListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}