package org.meveo.api.rest.knowledgeCenter.impl;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.knowledgeCenter.CollectionsResponseDto;
import org.meveo.api.dto.response.knowledgeCenter.GetCollectionResponseDto;
import org.meveo.api.knowledgeCenter.CollectionApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgeCenter.CollectionRs;
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
	public ActionStatus patch(CollectionDto postData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ActionStatus createOrUpdate(CollectionDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Collection collection = collectionApi.createOrUpdate(postData);
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(collection.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}


	@Override
	public GetCollectionResponseDto find(String code) {
		GetCollectionResponseDto result = new GetCollectionResponseDto();
		try {
			result.setCollection(collectionApi.findByCode(code));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ActionStatus remove(@PathParam("code") String code) {
		ActionStatus result = new ActionStatus();
		
		try {
			collectionApi.remove(code);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public CollectionsResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder ) {
			try {
				return collectionApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
			} catch (Exception e) {
				CollectionsResponseDto result = new CollectionsResponseDto();
				processException(e, result.getActionStatus());
				return result;
			}
	}

	@Override
	public CollectionsResponseDto tree() {
		try {
			return collectionApi.tree(null, new PagingAndFiltering());
		} catch (Exception e) {
			CollectionsResponseDto result = new CollectionsResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}

	
	
}
