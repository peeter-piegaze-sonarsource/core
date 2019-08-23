package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.PostsDto;
import org.meveo.api.dto.response.SearchResponse;

public class PostsResponseDto extends SearchResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7299389405567547296L;
	
	private PostsDto posts = new PostsDto();
	
	public PostsDto getPosts() {
		return posts;
	}

	public void setPosts(PostsDto posts) {
		this.posts = posts;
	}

	public String toString() {
        return "PostsResponseDto [posts=" + posts + ", toString()=" + super.toString() + "]";
	}
}
