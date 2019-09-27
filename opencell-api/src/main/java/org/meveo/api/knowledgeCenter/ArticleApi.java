package org.meveo.api.knowledgeCenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.knowledgeCenter.ArticleDto;
import org.meveo.api.dto.knowledgeCenter.ArticlesDto;
import org.meveo.api.dto.knowledgeCenter.ContentDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.knowledgeCenter.ArticlesResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.knowledgeCenter.Article;
import org.meveo.model.knowledgeCenter.Content;
import org.meveo.service.knowledgeCenter.ArticleService;
import org.primefaces.model.SortOrder;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ArticleApi extends BaseApi {
	@Inject
	ArticleService articleService;
	
	public Article create(ArticleDto postData) throws EntityDoesNotExistsException, MeveoApiException, BusinessException {
		Article article = new Article();
		Long parentId = postData.getParentId();
		Article parentArticle = null;
		
		if(parentId != null) {
			parentArticle = articleService.findById(parentId);
			if(parentArticle != null) {
				article.setParentArticle(parentArticle);
			}
			else {
				throw new EntityDoesNotExistsException("Parent article", parentId.toString());
			}
		}
		articleService.create(article);
		return article;
	}
	
	public Article update(ArticleDto postData) throws EntityDoesNotExistsException {
		Long articleId = postData.getId(); 
		if(StringUtils.isBlank(articleId)) {
			missingParameters.add("Id");
		}
		handleMissingParameters();
		
		Article article = articleService.findById(articleId);
		
		if(article == null) {
			throw new EntityDoesNotExistsException(Article.class, articleId.toString(), "id");
		}
		
		Long parentId = postData.getParentId();
		Article parentArticle;
		
		if(parentId != null) {
			parentArticle = articleService.findById(parentId);
			if(parentArticle == null) {
				throw new EntityDoesNotExistsException(Article.class, parentId);
			}
			else if (parentArticle == article) {
				throw new BusinessException("Article cannot contains itself");
			}
			else if(isLoop(article, parentArticle)) {
				throw new BusinessException("The article is looping on itself!");
			}
			else {
				article.setParentArticle(parentArticle);
			}
		}
		else {
			article.setParentArticle(null);
		}
		articleService.update(article);
		return article;
	}
	
	public Article createOrUpdate(ArticleDto postData) {
		if(postData.getId() == null) {
			return create(postData);
		}
		else {
			return update(postData);
		}
	}
	
	public ArticleDto findById(Long id) {
		if(id == null) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		
		Article article = articleService.findById(id);
		if(article == null) {
			throw new EntityDoesNotExistsException(Article.class, id);
		}
		

		ArticleDto articleDto = new ArticleDto(article, 1, false);
		
		return articleDto;
	}
	

	public ContentDto findLang(Long id, String languageCode) {
		Article article = articleService.findById(id);
		if(article == null) {
			throw new EntityDoesNotExistsException(Article.class, id);
		}
		ContentDto contentDto = null;
		List<Content> contents = new ArrayList<Content>();
		contents.addAll(article.getContents());
		for(Content c : contents) {
			if(c.getLanguage().getLanguageCode().equals(languageCode)){
				contentDto = new ContentDto(c);
			}
		}
		if(contentDto == null) {
			for(Content c : contents) {
				if(c.getLanguage().getLanguageCode().equals("ENG")){
					contentDto = new ContentDto(c);
				}
			}
		}
		if(contentDto == null) {
			for(Content c : contents) {
				if(c.getLanguage().getLanguageCode().equals("FRA")){
					contentDto = new ContentDto(c);
				}
			}
		}
		if(contentDto == null) {
			contentDto = new ContentDto(contents.get(0));
		}
		
		return contentDto;
		
	}
	
	public void remove(Long id) throws BusinessException{
		Article article = articleService.findById(id);
		
		if(article == null) {
			throw new EntityDoesNotExistsException(Article.class, id);
		}
		else if(!article.getChildrenArticle().isEmpty()) {
			throw new BusinessException("This Article still contains sub-articles");
		}
		else {
			articleService.remove(article);
		}
	}
	

	public ArticlesResponseDto list(ArticleDto postData, PagingAndFiltering pagingAndFiltering, int level, Boolean noContent, Boolean root) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("id", postData.getId());
		}
		
		PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null,
				pagingAndFiltering, Article.class);
		

		Long totalCount = articleService.count(paginationConfig);
		
		ArticlesDto articlesDto = new ArticlesDto();
		ArticlesResponseDto result = new ArticlesResponseDto();

		result.setPaging(pagingAndFiltering);
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		articlesDto.setTotalNumberOfRecords(totalCount);
		
		if (totalCount > 0) {
			List<Article> articles = articleService.list(paginationConfig);
			for (Article c : articles) {
				if(!root) {
					articlesDto.getArticle().add(new ArticleDto(c, level, noContent));
				}
				else if(c.getParentArticle() == null){
					articlesDto.getArticle().add(new ArticleDto(c, level, noContent));
				}
				else totalCount--;
			}
		}
		articlesDto.setTotalNumberOfRecords(totalCount);
		
		result.setArticles(articlesDto);
		
		return result;
	}
	
	public boolean isLoop(Article article, Article parentArticle) {
		Set<Long> articles =  new HashSet<Long>();
		articles.add(article.getId());
		
		article = parentArticle;
		
		while(article != null) {
			if(!articles.add(article.getId()))
				return true;
			article = article.getParentArticle();
		}
		return false;
	}
	
	public ArticlesResponseDto list(ArticleDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		return this.list(postData, pagingAndFiltering, 0, true, false);
	}

	public ArticlesResponseDto tree(ArticleDto postData, PagingAndFiltering pagingAndFiltering) {
		return this.list(postData, pagingAndFiltering, -1, true, true);
	}

}
