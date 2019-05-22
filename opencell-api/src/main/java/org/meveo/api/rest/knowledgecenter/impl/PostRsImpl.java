package org.meveo.api.rest.knowledgeCenter.impl;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.api.dto.response.knowledgeCenter.GetPostResponseDto;
import org.meveo.api.knowledgeCenter.CollectionApi;
import org.meveo.api.knowledgeCenter.PostApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgeCenter.PostRs;
import org.meveo.model.knowledgeCenter.Post;

public class PostRsImpl extends BaseRs implements PostRs {
	@Inject
	CollectionApi collectionApi;
	
	@Inject
	PostApi postApi;

	@Override
	public ActionStatus create(PostDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Post post = postApi.create(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(post.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(PostDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			postApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(PostDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Post post = postApi.createOrUpdate(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(post.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetPostResponseDto find(String code) {
		GetPostResponseDto result = new GetPostResponseDto();
		try {
			result.setPost(postApi.findByCode(code));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ActionStatus remove(String code) {
		ActionStatus result = new ActionStatus();

		try {
			postApi.remove(code);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}
}
