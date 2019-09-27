package org.meveo.api.rest.knowledgeCenter.impl;

import javax.inject.Inject;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.knowledgeCenter.ArticleDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.knowledgeCenter.ArticlesResponseDto;
import org.meveo.api.dto.response.knowledgeCenter.GetArticleResponseDto;
import org.meveo.api.dto.response.knowledgeCenter.GetContentResponseDto;
import org.meveo.api.knowledgeCenter.ArticleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.knowledgeCenter.ArticleRs;
import org.meveo.model.knowledgeCenter.Article;

public class ArticleRsImpl extends BaseRs implements ArticleRs {
	@Inject
	ArticleApi articleApi;
	
	@Override
	public ActionStatus create(ArticleDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Article article = articleApi.create(postData);
			result.setId(article.getId());
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(ArticleDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			articleApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(ArticleDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Article article = articleApi.createOrUpdate(postData);
			result.setId(article.getId());
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetArticleResponseDto find(Long id) {
		GetArticleResponseDto result = new GetArticleResponseDto();
		try {
			result.setArticle(articleApi.findById(id));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ActionStatus remove(Long id) {
		ActionStatus result = new ActionStatus();
		try {
			articleApi.remove(id);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ArticlesResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder) {
		try {
			return articleApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
		} catch (Exception e) {
			ArticlesResponseDto result = new ArticlesResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}

	@Override
	public GetContentResponseDto findLang(Long id, String languageCode) {
		GetContentResponseDto result = new GetContentResponseDto();
		try {
			result.setContent(articleApi.findLang(id, languageCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ArticlesResponseDto tree() {
		try {
			return articleApi.tree(null, new PagingAndFiltering());
		} catch (Exception e) {
			ArticlesResponseDto result = new ArticlesResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}
	
}
