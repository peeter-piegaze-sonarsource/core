package org.meveo.api.dto.knowledgeCenter;

import java.util.HashSet;
import java.util.Set;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.knowledgeCenter.Comment;
import org.meveo.model.knowledgeCenter.Post;

public class PostDto extends BusinessEntityDto {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3588467558706095289L;
	
	private String name;
	private String content;
	private String collection;
	private Set<String> tags = new HashSet<String>();
	private Set<CommentDto> comments;
	
	
	public PostDto () {
		
	}
	
	public PostDto(Post post) {
		super(post);
		name = post.getName();
		content = post.getContent();
		collection = post.getCollection().getCode();
		comments = new HashSet<CommentDto>();
		Set<Comment> postComments = post.getCommments();
		for(Comment c : postComments) {
			comments.add(new CommentDto(c));
		}
		tags = post.getTags();
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

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	

	public Set<CommentDto> getComments() {
		return comments;
	}

	public void setComments(Set<CommentDto> comments) {
		this.comments = comments;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	
}
