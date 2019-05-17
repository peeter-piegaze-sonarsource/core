package org.meveo.api.rest.knowledgecenter.impl;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.dto.response.knowledgecenter.GetCollectionResponseDto;
import org.meveo.api.knowledgecenter.CollectionApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgecenter.CollectionRs;
import org.meveo.model.knowledgeCenter.Collection;

public class CollectionRsImpl extends BaseRs implements CollectionRs{
	@Inject
	CollectionApi collectionApi;
	
	@Override
	public ActionStatus create(CollectionDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Collection collection = collectionApi.create(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(collection.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(CollectionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			collectionApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetCollectionResponseDto find(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus remove(String code) {
		// TODO Auto-generated method stub
		return null;
	}

}
