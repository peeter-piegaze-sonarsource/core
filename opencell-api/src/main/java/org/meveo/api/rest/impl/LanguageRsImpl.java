package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.LanguageApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.response.GetTradingLanguageResponse;
import org.meveo.api.dto.response.LanguagesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.LanguageRs;

/**
 * @author Edward P. Legaspi
 * 
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class LanguageRsImpl extends BaseRs implements LanguageRs {

    @Inject
    private LanguageApi languageApi;

    @Override
    public ActionStatus create(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTradingLanguageResponse find(String languageCode) {
        GetTradingLanguageResponse result = new GetTradingLanguageResponse();

        try {
            result.setLanguage(languageApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

	@Override
	public LanguagesResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder) {
		try {
			return languageApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
		} catch (Exception e) {
			LanguagesResponseDto result = new LanguagesResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}
}
