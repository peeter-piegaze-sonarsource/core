package org.meveo.api.knowledgeCenter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.knowledgeCenter.Post;
import org.meveo.service.knowledgecenter.CollectionService;
import org.meveo.service.knowledgecenter.PostService;


@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PostApi extends BaseApi{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7266727869936789977L;

	@Inject
	CollectionService collectionService;
	
	@Inject
	PostService postService;
	
	public Post create(PostDto postData) throws BusinessException, MissingParameterException {
		if(StringUtils.isBlank(postData.getName())) {
			missingParameters.add("Name");
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		
		Post post = new Post();
		post.setName(postData.getName());
		post.setContent(postData.getContent());
		post.setCode(postData.getCode());
		post.setDescription(postData.getDescription());
		post.setCollection(collectionService.findByCode(postData.getCollection()));
		
		postService.create(post);
		
		return post;
	}
	
	public Post update(PostDto postData) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		handleMissingParameters();
		
		String code = postData.getCode();
		
		Post post = postService.findByCode(code);
		
		if(post == null) {
			throw new EntityDoesNotExistsException(Post.class, code, "code");
		}

		if(!StringUtils.isBlank(postData.getName()))
			post.setName(postData.getName());
		if(!StringUtils.isBlank(postData.getContent()))
			post.setContent(postData.getContent());
		if(!StringUtils.isBlank(postData.getCollection()))
			post.setCollection(collectionService.findByCode(postData.getCollection()));
		
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
}
