package org.meveo.api.rest.knowledgeCenter.impl;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.CommentDto;
import org.meveo.api.dto.response.knowledgeCenter.GetCommentResponseDto;
import org.meveo.api.knowledgeCenter.CommentApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgeCenter.CommentRs;
import org.meveo.model.knowledgeCenter.Comment;

public class CommentRsImpl extends BaseRs implements CommentRs {
	@Inject
	CommentApi commentApi;

	@Override
	public ActionStatus create(CommentDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Comment comment = commentApi.create(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(comment.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(CommentDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			commentApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(CommentDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Comment comment = commentApi.createOrUpdate(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(comment.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetCommentResponseDto find(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus remove(String code) {
		ActionStatus result = new ActionStatus();
		try {
			commentApi.remove(code);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}
}
