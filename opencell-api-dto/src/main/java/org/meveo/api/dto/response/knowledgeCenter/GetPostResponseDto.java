package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetPostResponseDto extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4493946979588987787L;
	
	private PostDto post;
	
	public PostDto getPost() {
		return post;
	}
	
	public void setPost(PostDto post) {
		this.post = post;
	}
}
