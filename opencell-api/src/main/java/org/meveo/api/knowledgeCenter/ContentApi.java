package org.meveo.api.knowledgeCenter;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.knowledgeCenter.ContentDto;
import org.meveo.api.dto.knowledgeCenter.ContentsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.knowledgeCenter.ContentsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Language;
import org.meveo.model.knowledgeCenter.Article;
import org.meveo.model.knowledgeCenter.Content;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.knowledgeCenter.ArticleService;
import org.meveo.service.knowledgeCenter.ContentService;
import org.primefaces.model.SortOrder;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContentApi  extends BaseApi {
	@Inject
	ContentService contentService;

	@Inject
	ArticleService articleService;

	@Inject
	LanguageService languageService;
	
	public Content create(ContentDto postData) {
		
		if(postData.getParentId() == null) {
			missingParameters.add("ParentId");
		}
		if(StringUtils.isBlank(postData.getLanguageCode())) {
			missingParameters.add("LanguageCode");
		}
		if(StringUtils.isBlank(postData.getTitle())) {
			missingParameters.add("Title");
		}
		if(StringUtils.isBlank(postData.getContent())) {
			missingParameters.add("Content");
		}
		handleMissingParameters();
		
		Content content = new Content();
		Long parentId = postData.getParentId();
		String languageCode = postData.getLanguageCode();
		Article parentArticle = articleService.findById(parentId);
		Language language = languageService.findByCode(languageCode);
		

		if(parentArticle == null) {
			throw new EntityDoesNotExistsException(Article.class, parentId);
		}
		if(language == null) {
			throw new EntityDoesNotExistsException(Language.class, languageCode);
		}
		for(Content pc : parentArticle.getContents()){
			if(language.equals(pc.getLanguage()))
				throw new BusinessException("This language already exist in this article. Please use update API.");
		}
		content.setLanguage(language);
		content.setTitle(postData.getTitle());
		content.setContent(postData.getContent());
		content.setArticle(parentArticle);
		content.setArticle(parentArticle);
		
		contentService.create(content);
		
		return content;
	}
	
	public Content update(ContentDto postData) {
		if(postData.getId() == null) {
			missingParameters.add("Id");
		}
		if(postData.getParentId() == null) {
			missingParameters.add("ParentId");
		}
		if(StringUtils.isBlank(postData.getLanguageCode())) {
			missingParameters.add("LanguageCode");
		}
		if(StringUtils.isBlank(postData.getTitle())) {
			missingParameters.add("Title");
		}
		if(StringUtils.isBlank(postData.getContent())) {
			missingParameters.add("Content");
		}
		handleMissingParameters();
		
		Long id = postData.getId();
		
		Content content = contentService.findById(id);
		if(content == null) {
			throw new EntityDoesNotExistsException(Content.class, id);
		}
		Long parentId = postData.getParentId();
		String languageCode = postData.getLanguageCode();
		Article parentArticle = articleService.findById(parentId);
		Language language = languageService.findByCode(languageCode);
		

		if(parentArticle == null) {
			throw new EntityDoesNotExistsException(Article.class, parentId);
		}
		if(language == null) {
			throw new EntityDoesNotExistsException(Language.class, languageCode);
		}
		for(Content pc : parentArticle.getContents()){
			if(language.equals(pc.getLanguage()))
				throw new BusinessException("This language already exist in this article. Please use update API.");
		}
		
		content.setLanguage(language);
		content.setTitle(postData.getTitle());
		content.setContent(postData.getContent());
		content.setArticle(parentArticle);
		content.setArticle(parentArticle);
		
		contentService.update(content);
		
		return content;
	}
	
	public Content createOrUpdate(ContentDto postData) throws EntityDoesNotExistsException {
		if(postData.getId() == null) {
			return create(postData);
		}
		else {
			return update(postData);
		}
	}
	
	public ContentDto findById(Long id) {
		if(id == null) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		
		Content content = contentService.findById(id);
		if(content == null) {
			throw new EntityDoesNotExistsException(Content.class, id);
		}
		
		ContentDto contentDto = new ContentDto(content);
		
		return contentDto;
	}
	
	public void remove(Long id) throws BusinessException {
		Content content = contentService.findById(id);
		
		if(content == null) {
			throw new EntityDoesNotExistsException(Content.class, id);
		}
		else {
			contentService.remove(content);
		}
	}
	
	public ContentsResponseDto list(ContentDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("id", postData.getId());
		}
		
		PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null,
				pagingAndFiltering, Article.class);
		

		Long totalCount = contentService.count(paginationConfig);
		
		ContentsDto contentsDto = new ContentsDto();
		ContentsResponseDto result = new ContentsResponseDto();

		result.setPaging(pagingAndFiltering);
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		contentsDto.setTotalNumberOfRecords(totalCount);
		
		if (totalCount > 0) {
			List<Content> contents = contentService.list(paginationConfig);
			for (Content c : contents) {
				contentsDto.getContents().add(new ContentDto(c));
			}
		}
		result.setContents(contentsDto);
		return result;
	}
	
}
