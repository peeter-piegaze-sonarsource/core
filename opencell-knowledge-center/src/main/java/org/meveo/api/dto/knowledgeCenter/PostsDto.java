package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostsDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8219045867902551652L;

	private List<PostDto> post;
	
	private Long totalNumberOfRecords;

	public PostsDto() {

	}
	
	public List<PostDto> getPost(){
		if(post == null) {
			post = new ArrayList<PostDto>();
		}
		
		return post;
	}

	
	public void setPost(List<PostDto> post) {
		this.post = post;
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public String toString() {
		return "PostsDto [post=" + post + "]";
	}
}
