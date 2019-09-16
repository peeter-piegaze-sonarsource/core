package org.meveo.api.knowledgeCenter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.dto.knowledgeCenter.CollectionsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.knowledgeCenter.CollectionsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.service.knowledgeCenter.CollectionService;
import org.meveo.service.knowledgeCenter.PostService;
import org.primefaces.model.SortOrder;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CollectionApi extends BaseApi {
	@Inject
	CollectionService collectionService;
	
	@Inject
	PostService postService;

	public Collection create(CollectionDto postData) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
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
		collection.setDescription(postData.getDescription());
		String parentCode = postData.getParentCode();
		if(!StringUtils.isBlank(parentCode)) {
			Collection parentCollection = collectionService.findByCode(parentCode);
			
			if(parentCode == postData.getCode()) {
				throw new BusinessException("Collection cannot contains itself");
			}
			else if (parentCollection == null) {
				throw new EntityDoesNotExistsException("Parent Collection", parentCode);
			}
			else if(isLoop(collection, parentCollection)) {
				throw new BusinessException("The collection is looping on itself!");
			}
			else {
				collection.setParentCollection(parentCollection);
			}
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
		
		if(!StringUtils.isBlank(postData.getName()))
			collection.setName(postData.getName());
		if(!StringUtils.isBlank(postData.getDescription()))
			collection.setDescription(postData.getDescription());

		String parentCode = postData.getParentCode();
		if(!StringUtils.isBlank(parentCode)) {
			Collection parentCollection = collectionService.findByCode(parentCode);
			if (parentCollection == null) {
				throw new EntityDoesNotExistsException("Parent Collection", parentCode);
			}
			else if(parentCollection == collection) {
				throw new BusinessException("Collection cannot contains itself");
			}
			else if(isLoop(collection, parentCollection)) {
				throw new BusinessException("The collection is looping on itself!");
			}
			else {
				collection.setParentCollection(parentCollection);
			}
		}
		else {
			collection.setParentCollection(null);
		}
		
		collectionService.update(collection);
		
		return collection;
	}
	
	public Collection createOrUpdate(CollectionDto postData) throws MeveoApiException, BusinessException {
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}

		handleMissingParameters();
		
		String code = postData.getCode();
		Collection collection = collectionService.findByCode(code);
		if(collection == null) {
			return create(postData);
		}
		else {
			return update(postData);
		}
	}
	
	public CollectionDto findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();
		
		CollectionDto collectionDto = null;
		Collection collection = collectionService.findByCode(code);
		
		if(collection == null) {
			throw new EntityDoesNotExistsException(Collection.class, code, "code");
		}
		
		collectionDto = new CollectionDto(collection);
		
		return collectionDto;

	}

	public void remove(String code) throws BusinessException, EntityDoesNotExistsException {
		Collection collection = collectionService.findByCode(code);
		Collection collectionParent = null;
		if (collection == null) {
			throw new EntityDoesNotExistsException(Collection.class, code, "code");
		}
		else {
			if(!collection.getChildrenCollections().isEmpty() || !collection.getPosts().isEmpty()) {
				throw new BusinessException("Collection code:" + collection.getCode() + " still contains collections or posts");
			}
			collectionParent = collection.getParentCollection();
			if(collectionParent != null) {
				collectionParent.getChildrenCollections().remove(collection);
				collectionService.update(collectionParent);
			}
		}
		
		collectionService.remove(collection);
	}
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "collections.collection", itemPropertiesToFilter = {
			@FilterProperty(property = "code", entityClass = Collection.class) })
	public CollectionsResponseDto list(CollectionDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("code", postData.getCode());
		}

		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null,
				pagingAndFiltering, Collection.class);

		Long totalCount = collectionService.count(paginationConfig);

		CollectionsDto collectionsDto = new CollectionsDto();
		CollectionsResponseDto result = new CollectionsResponseDto();

		result.setPaging(pagingAndFiltering);
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		collectionsDto.setTotalNumberOfRecords(totalCount);

		if (totalCount > 0) {
			List<Collection> collections = collectionService.list(paginationConfig);
			for (Collection c : collections) {
				c.setChildrenCollections(null);
				collectionsDto.getCollection().add(new CollectionDto(c));
			}
		}
		result.setCollections(collectionsDto);
		return result;
	}
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "collections.collection", itemPropertiesToFilter = {
			@FilterProperty(property = "code", entityClass = Collection.class) })
	public CollectionsResponseDto tree(CollectionDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("code", postData.getCode());
		}

		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null,
				pagingAndFiltering, Collection.class);

		Long totalCount = collectionService.count(paginationConfig);

		CollectionsDto collectionsDto = new CollectionsDto();
		CollectionsResponseDto result = new CollectionsResponseDto();

		result.setPaging(pagingAndFiltering);

		if (totalCount > 0) {
			List<Collection> collections = collectionService.list(paginationConfig);
			for (Collection c : collections) {
				if(c.getParentCollection() == null) {
					collectionsDto.getCollection().add(new CollectionDto(c));
					
				}
				else totalCount--;
			}
		}
		
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		collectionsDto.setTotalNumberOfRecords(totalCount);
		
		result.setCollections(collectionsDto);
		return result;
	}
	
	public boolean isLoop(Collection collection, Collection parentCollection) {
		Set<String> collections =  new HashSet<String>();
		collections.add(collection.getCode());
		
		collection = parentCollection;
		
		while(collection != null) {
			if(!collections.add(collection.getCode()))
				return true;
			collection = collection.getParentCollection();
		}
		return false;
	}
}
