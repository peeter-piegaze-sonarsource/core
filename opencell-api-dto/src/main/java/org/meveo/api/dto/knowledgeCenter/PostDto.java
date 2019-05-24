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
	private Set<Comment> comments;
	
	
	public PostDto () {
		
	}
	
	public PostDto(Post post) {
		super(post);
		content = post.getContent();
		collection = post.getCollection().getName();
		comments = post.getCommments();
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

	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	
}
