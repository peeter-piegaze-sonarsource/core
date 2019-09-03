package org.meveo.api.knowledgeCenter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.knowledgeCenter.CommentDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.knowledgeCenter.Comment;
import org.meveo.model.knowledgeCenter.Lang;
import org.meveo.model.knowledgeCenter.MarkdownContent;
import org.meveo.model.knowledgeCenter.Post;
import org.meveo.service.knowledgeCenter.CommentService;
import org.meveo.service.knowledgeCenter.LangService;
import org.meveo.service.knowledgeCenter.PostService;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CommentApi extends BaseApi {
	@Inject
	CommentService commentService;
	
	@Inject
	PostService postService;

	@Inject
	LangService languageService;
	
	public Comment create(CommentDto postData) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(postData.getContent())) {
			missingParameters.add("Content");
		}
		if(StringUtils.isBlank(postData.getLanguage())) {
			missingParameters.add("Language");
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		if(StringUtils.isBlank(postData.getPostCode())) {
			missingParameters.add("PostCode");
		}
		handleMissingParameters();
		
		Lang language = languageService.findByCode(postData.getLanguage());
		MarkdownContent mdc = new MarkdownContent(null, postData.getContent(), language);
		
		
		Comment comment = new Comment();
		comment.setCode(postData.getCode());
		comment.setMarkdownContent(mdc);
		comment.setDescription(postData.getDescription());
		String postCode = postData.getPostCode();
		Post post = null;
		if(!StringUtils.isBlank(postCode)) {
			post = postService.findByCode(postCode);
			if(post == null) {
				throw new EntityDoesNotExistsException("Parent Post", postCode);
			}
			else {
				comment.setPost(post);
			}
		}
		commentService.create(comment);
		post.getCommments().add(comment);
		postService.update(post);
		return comment;
	}
	
	public Comment update(CommentDto postData) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(!StringUtils.isBlank(postData.getContent()) ||
				!StringUtils.isBlank(postData.getLanguage())) {
			if(StringUtils.isBlank(postData.getContent())) {
				missingParameters.add("Content");
			}
			if(StringUtils.isBlank(postData.getLanguage())) {
				missingParameters.add("Language");
			}
		}
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}
		if(StringUtils.isBlank(postData.getPostCode())) {
			missingParameters.add("PostCode");
		}
		handleMissingParameters();
		
		Comment comment = commentService.findByCode(postData.getCode());
		if(comment != null) {
			Lang language = languageService.findByCode(postData.getLanguage());
			comment.setMarkdownContent(new MarkdownContent(null, postData.getContent(), language));
			comment.setDescription(postData.getDescription());
			if(!comment.getPost().getCode().equals(postData.getPostCode()))
				throw new BusinessException("Parent post code cannot be changed!");
		}
		else {
			throw new EntityDoesNotExistsException("Comment", postData.getCode());
		}
		
		commentService.update(comment);
		return comment;
	}
	
	public Comment createOrUpdate(CommentDto postData) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("Code");
		}

		handleMissingParameters();

		String code = postData.getCode();
		Comment comment = commentService.findByCode(code);
		if(comment == null) {
			return create(postData);
		}
		else {
			return update(postData);
		}
		
	}
	
	public CommentDto findByCode(String code) throws MissingParameterException, EntityDoesNotExistsException {
		if(StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}
		
		handleMissingParameters();
		
		CommentDto commentDto = null;
		Comment comment = commentService.findByCode(code);
		
		if(comment == null) {
			throw new EntityDoesNotExistsException(Comment.class, code, "code");
		}
		
		commentDto = new CommentDto(comment);
		
		return commentDto;
	}
	
	public void remove (String code) throws EntityDoesNotExistsException, BusinessException {
		Comment comment = commentService.findByCode(code);
		
		if(comment == null) {
			throw new EntityDoesNotExistsException(Comment.class, code, "code");
		}
		
		commentService.remove(comment);
	}
	
}
