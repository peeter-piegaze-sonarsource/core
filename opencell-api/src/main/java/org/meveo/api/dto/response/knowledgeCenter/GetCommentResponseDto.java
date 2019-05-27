package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.CommentDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetCommentResponseDto  extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147315332438166828L;
	
	private CommentDto comment;

	public CommentDto getComment() {
		return comment;
	}

	public void setComment(CommentDto comment) {
		this.comment = comment;
	}
}
