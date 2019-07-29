package org.meveo.api.dto.knowledgeCenter;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.knowledgeCenter.Comment;

public class CommentDto extends BusinessEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6224893272089126086L;
	
	private String postCode;
	private MarkdownContentDto markdownContent;
	private String name;
	private String content;
	private String language;
	
	public CommentDto() {

	}
	
	public CommentDto(Comment comment) {
		super(comment);
		postCode = comment.getPost().getCode();
		markdownContent = new MarkdownContentDto(comment.getMarkdownContent());
	}


	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public MarkdownContentDto getMarkdownContent() {
		return markdownContent;
	}

	public void setMarkdownContent(MarkdownContentDto markdownContent) {
		this.markdownContent = markdownContent;
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
