package org.meveo.api.rest.knowledgeCenter.impl;

import javax.inject.Inject;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.ContentDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.knowledgeCenter.ContentsResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.knowledgeCenter.GetContentResponseDto;
import org.meveo.api.knowledgeCenter.ContentApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgeCenter.ContentRs;
import org.meveo.model.knowledgeCenter.Content;

public class ContentRsImpl extends BaseRs implements ContentRs {
	@Inject
	ContentApi contentApi;
	
	@Override
	public ActionStatus create(ContentDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Content content = contentApi.create(postData);
			result.setEntityId(content.getId());
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(ContentDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			contentApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(ContentDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Content content = contentApi.createOrUpdate(postData);
			result.setEntityId(content.getId());
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetContentResponseDto find(Long id) {
		GetContentResponseDto result = new GetContentResponseDto();
		try {
			result.setContent(contentApi.findById(id));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ActionStatus remove(Long id) {
		ActionStatus result = new ActionStatus();
		try {
			contentApi.remove(id);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ContentsResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder) {
		try {
			return contentApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
		} catch (Exception e) {
			ContentsResponseDto result = new ContentsResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}

}
