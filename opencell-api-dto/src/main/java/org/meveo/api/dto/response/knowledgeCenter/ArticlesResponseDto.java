package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.ArticlesDto;
import org.meveo.api.dto.response.SearchResponse;

public class ArticlesResponseDto extends SearchResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6418216208101124448L;
	
	private ArticlesDto articles = new ArticlesDto();

	public ArticlesDto getArticles() {
		return articles;
	}

	public void setArticles(ArticlesDto articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return "ArticlesResponseDto []";
	}
}
