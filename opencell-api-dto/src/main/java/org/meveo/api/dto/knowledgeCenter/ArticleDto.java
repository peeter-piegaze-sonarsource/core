package org.meveo.api.dto.knowledgeCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.knowledgeCenter.Article;
import org.meveo.model.knowledgeCenter.Content;

public class ArticleDto extends AuditableEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -804506608676110861L;

	private Long id;
	
	private List<ArticleDto> childrenArticles;

	private Long parentId;
	
	private List<ContentDto> contents;

	public ArticleDto() {

	}
	
	public ArticleDto(Article article) {
		this(article, 1, false);
	}
	
	public ArticleDto(Article article, int level, Boolean noContent) {
		super(article);
		id = article.getId();
		Set<Article> childrenArticles = article.getChildrenArticle();
		Set<Content> contents = article.getContents();
		Article parent = article.getParentArticle();
		
		if(parent != null)
			parentId = parent.getId();
		
		if(childrenArticles != null && (level > 0 || level < 0)) {
			this.childrenArticles = new ArrayList<ArticleDto>();
			for(Article a : childrenArticles) {
				this.childrenArticles.add(new ArticleDto(a, level-1, true));
			}
		}
		
		if(contents != null) {
			this.contents = new ArrayList<ContentDto>();
			for(Content c : contents) {
				this.contents.add(new ContentDto(c, noContent));
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ArticleDto> getChildrenArticles() {
		return childrenArticles;
	}

	public void setChildrenArticles(List<ArticleDto> childrenArticles) {
		this.childrenArticles = childrenArticles;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<ContentDto> getContents() {
		return contents;
	}

	public void setContents(List<ContentDto> contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "ArticleDto [parentId=" + parentId + ", contents=" + contents + "]";
	}
	
	
	
}
