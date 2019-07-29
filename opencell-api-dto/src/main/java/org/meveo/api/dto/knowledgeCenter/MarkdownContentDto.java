package org.meveo.api.dto.knowledgeCenter;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.knowledgeCenter.MarkdownContent;

public class MarkdownContentDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2958808175822021619L;
	
	
	private String name;
	private String content;
	private String language;
	
	public MarkdownContentDto() {
		
	}
	
	public MarkdownContentDto(MarkdownContent markdownContent) {
		name = markdownContent.getName();
		content = markdownContent.getContent();
		language = markdownContent.getLanguage().getLanguageCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
