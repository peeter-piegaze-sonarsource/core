package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticlesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6967664766228749309L;

	private List<ArticleDto> articles;
	
	private Long totalNumberOfRecords;
	
	public ArticlesDto() {
		
	}

	public List<ArticleDto> getArticles() {
		if(articles == null) {
			articles = new ArrayList<ArticleDto>();
		}
		
		return articles;
	}

	public void setArticles(List<ArticleDto> articles) {
		this.articles = articles;
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	@Override
	public String toString() {
		return "ArticlesDto [articles=" + articles + ", totalNumberOfRecords=" + totalNumberOfRecords + "]";
	}
	
	
}
