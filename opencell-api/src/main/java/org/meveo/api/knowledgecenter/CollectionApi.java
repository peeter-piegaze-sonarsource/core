package org.meveo.api.knowledgeCenter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.service.knowledgecenter.CollectionService;
import org.meveo.service.knowledgecenter.PostService;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CollectionApi extends BaseApi {
	@Inject
	CollectionService collectionService;
	
	@Inject
	PostService postService;

	public Collection create(CollectionDto postData) throws BusinessException, MissingParameterException {
		if(StringUtils.isBlank(postData.getName())) {
			missingParameters.add("Name");
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		Collection collection = new Collection();
		collection.setName(postData.getName());
		collection.setCode(postData.getCode());
		if(!StringUtils.isBlank(postData.getParentCollectionCode())) {
			String parentCollectionCode = postData.getParentCollectionCode();
			Collection parentCollection = collectionService.findByCode(parentCollectionCode);
			collection.setParentCollection(parentCollection);
		}
		collectionService.create(collection);
		return collection;
	}
	
	public Collection update(CollectionDto postData) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		
		String code = postData.getCode();
		
		Collection collection = collectionService.findByCode(code);
		
		if (collection == null) {
			throw new EntityDoesNotExistsException(Collection.class, code, "code");
		}
		
		collection.setName(postData.getName());
		
		collectionService.update(collection);
		
		return collection;
	}
	
	
}
