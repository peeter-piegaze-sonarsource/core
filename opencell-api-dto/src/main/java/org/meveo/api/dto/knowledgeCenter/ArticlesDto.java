package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticlesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6967664766228749309L;

	private List<ArticleDto> article;
	
	private Long totalNumberOfRecords;
	
	public ArticlesDto() {
		
	}

	public List<ArticleDto> getArticle() {
		if(article == null) {
			article = new ArrayList<ArticleDto>();
		}
		
		return article;
	}

	public void setArticle(List<ArticleDto> article) {
		this.article = article;
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	@Override
	public String toString() {
		return "ArticlesDto [articles=" + article + ", totalNumberOfRecords=" + totalNumberOfRecords + "]";
	}
	
	
}
