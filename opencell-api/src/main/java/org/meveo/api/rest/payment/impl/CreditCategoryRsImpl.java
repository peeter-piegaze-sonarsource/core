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

package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CreditCategoryApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.CreditCategoryRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CreditCategory;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @since 22 Aug 2017
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CreditCategoryRsImpl extends BaseRs implements CreditCategoryRs {

	@Inject
	private CreditCategoryApi creditCategoryApi;

	@Override
	public ActionStatus create(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			CreditCategory creditCategory = creditCategoryApi.create(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(creditCategory.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus update(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}
	
	@Override
	public ActionStatus createOrUpdate(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			CreditCategory creditCategory = creditCategoryApi.createOrUpdate(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(creditCategory.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public CreditCategoryResponseDto find(String creditCategoryCode) {
		CreditCategoryResponseDto result = new CreditCategoryResponseDto();

		try {
			result.setCreditCategory(creditCategoryApi.find(creditCategoryCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public CreditCategoriesResponseDto list() {
		CreditCategoriesResponseDto result = new CreditCategoriesResponseDto();

		try {
			result.setCreditCategories(creditCategoryApi.list());
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus remove(String creditCategoryCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.remove(creditCategoryCode);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

}
