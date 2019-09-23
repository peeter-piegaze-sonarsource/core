package org.meveo.api.dto.knowledgeCenter;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.billing.Language;
import org.meveo.model.knowledgeCenter.Content;

public class ContentDto extends AuditableEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9075219944438276373L;
	
	private Long id;
	
	private String title;
	
	private String content;
	
	private String languageCode;
	
	private Long parentId;

	
	public ContentDto() {

	}
	
	public ContentDto(Content content) {
		this(content, false);
	}
	
	public ContentDto(Content content, Boolean noContent) {
		super(content);
		id = content.getId();
		title = content.getTitle();
		if(!noContent)
			this.content = content.getContent();
		Language language = content.getLanguage();
		this.parentId = content.getArticle().getId();
		
		if(language != null)
			languageCode = language.getLanguageCode();
	}
	

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getLanguageCode() {
		return languageCode;
	}


	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Long getParentId() {
		return parentId;
	}


	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}


	@Override
	public String toString() {
		return "ContentDto [title=" + title + ", languageCode=" + languageCode + "]";
	}

}
