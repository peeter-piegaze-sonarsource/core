package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.ArticleDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetArticleResponseDto extends BaseResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4056250137263910208L;
	private ArticleDto article;
	
	public ArticleDto getArticle() {
		return article;
	}
	public void setArticle(ArticleDto article) {
		this.article = article;
	}

	
	
}
