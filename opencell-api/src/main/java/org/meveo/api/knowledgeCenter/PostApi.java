package org.meveo.api.knowledgeCenter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.lucene.util.packed.PackedLongValues.Iterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.api.dto.knowledgeCenter.PostsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.knowledgeCenter.PostsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Language;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.model.knowledgeCenter.MarkdownContent;
import org.meveo.model.knowledgeCenter.Post;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.knowledgeCenter.CollectionService;
import org.meveo.service.knowledgeCenter.PostService;
import org.primefaces.model.SortOrder;


@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PostApi extends BaseApi{
	@Inject
	CollectionService collectionService;
	
	@Inject
	PostService postService;
	
	@Inject
	LanguageService languageService;
	
	public Post create(PostDto postData) throws BusinessException, MissingParameterException {
		if(StringUtils.isBlank(postData.getName())) {
			missingParameters.add("Name");
		}
		if(StringUtils.isBlank(postData.getContent())) {
			missingParameters.add("Content");
		}
		if(StringUtils.isBlank(postData.getLanguage())) {
			missingParameters.add("Language");
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		
		Post post = new Post();
		
		Set<MarkdownContent> markdownContents = new HashSet<MarkdownContent>();
		Language language = languageService.findByCode(postData.getLanguage());
		MarkdownContent mdc = new MarkdownContent(postData.getName(), postData.getContent(), language);
		markdownContents.add(mdc);
		post.setCode(postData.getCode());
		post.setDescription(postData.getDescription());
		
		if(!StringUtils.isBlank(postData.getCollection())) {
			Collection collection = collectionService.findByCode(postData.getCollection());
			if(collection != null)
				post.setCollection(collection);
			else {
				throw new EntityDoesNotExistsException("Parent Collection", postData.getCollection());
			}
		}
		else {
			throw new BusinessException("Parent collection is not provided!");
		}
		
		postService.create(post);
		
		return post;
	}
	
	public Post update(PostDto postData) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
		Boolean hasContent = false;
		if(!StringUtils.isBlank(postData.getName()) ||
				!StringUtils.isBlank(postData.getContent()) ||
				!StringUtils.isBlank(postData.getLanguage())) {
			if(StringUtils.isBlank(postData.getName())) {
				missingParameters.add("Name");
			}
			if(StringUtils.isBlank(postData.getContent())) {
				missingParameters.add("Content");
			}
			if(StringUtils.isBlank(postData.getLanguage())) {
				missingParameters.add("Language");
			}
			hasContent = true;
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		
		String code = postData.getCode();
		
		Post post = postService.findByCode(code);
		
		if(post == null) {
			throw new EntityDoesNotExistsException(Post.class, code, "code");
		}
		
		if(hasContent) {
			Set<MarkdownContent> mdcs = post.getMarkdownContents();
			Language language = languageService.findByCode(postData.getLanguage());
			Boolean inSet = false;
			for(MarkdownContent mdc : mdcs) {
				if(mdc.getLanguage().equals(language)) {
					mdc.setName(postData.getName());
					mdc.setContent(postData.getContent());
					inSet = true;
				}
			}
			if(!inSet)
			mdcs.add(new MarkdownContent(postData.getName(), postData.getContent(), language));
		}
		
		if(!StringUtils.isBlank(postData.getCollection())) {
			Collection collection = collectionService.findByCode(postData.getCollection());
			if(collection != null)
				post.setCollection(collection);
			else {
				throw new EntityDoesNotExistsException("Parent Collection", postData.getCollection());
			}
		}
		
		postService.update(post);
		
		return post;
	}
	
	public Post createOrUpdate(PostDto postData) throws MeveoApiException, BusinessException {
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}

		handleMissingParameters();
		
		String code = postData.getCode();
		Post post = postService.findByCode(code);
		if(post == null) {
			return create(postData);
		}
		else {
			return update(postData);
		}
	}
	
	public PostDto findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();
		
		PostDto postDto = null;
		Post post = postService.findByCode(code);
		
		if(post == null) {
			throw new EntityDoesNotExistsException(Post.class, code, "code");
		}
		
		postDto = new PostDto(post);
		
		return postDto;

	}

	public void remove(String code) throws BusinessException, EntityDoesNotExistsException {
		Post post = postService.findByCode(code);

		if (post == null) {
			throw new EntityDoesNotExistsException(Post.class, code, "code");
		}

		postService.remove(post);
	}
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "posts.post", itemPropertiesToFilter = {
			@FilterProperty(property = "code", entityClass = Post.class) })
	public PostsResponseDto list(PostDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("code", postData.getCode());
		}

		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null,
				pagingAndFiltering, Post.class);

		Long totalCount = postService.count(paginationConfig);

		PostsDto postsDto = new PostsDto();
		PostsResponseDto result = new PostsResponseDto();

		result.setPaging(pagingAndFiltering);
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		postsDto.setTotalNumberOfRecords(totalCount);

		if (totalCount > 0) {
			List<Post> posts = postService.list(paginationConfig);
			for (Post p : posts) {
				postsDto.getPost().add(new PostDto(p));
			}
		}
		result.setPosts(postsDto);
		return result;
	}
}
