package org.meveo.api.dto.knowledgeCenter;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.BusinessEntity;
import org.meveo.model.knowledgeCenter.Comment;
import org.meveo.model.knowledgeCenter.Post;

public class CommentDto extends BusinessEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6224893272089126086L;
	
	private String postCode;
	
	private String content;

	
	public CommentDto() {

	}
	
	public CommentDto(Comment comment) {
		super(comment);
		postCode = comment.getPost().getCode();
		content = comment.getContent();
	}

	public CommentDto(BusinessEntity e) {
		super(e);
		// TODO Auto-generated constructor stub
	}


	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
