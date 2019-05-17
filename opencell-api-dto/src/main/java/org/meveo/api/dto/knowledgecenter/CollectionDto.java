package org.meveo.api.dto.knowledgeCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.knowledgeCenter.PostDto;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.model.knowledgeCenter.Post;

public class CollectionDto extends BusinessEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4889360284187982497L;

	private List<String> childrenCollectionsCode;
	
	private String parentCollectionCode;
	
	private List<PostDto> posts;
	
	private String name;
	
	public CollectionDto() {
		
	}
	
	public CollectionDto(Collection collection) {
		Set<Collection> childrenCollections = collection.getChildrenCollections();
		Collection parentCollection = collection.getParentCollection();
		Set<Post> postsSet = collection.getPosts();
		
		childrenCollectionsCode = new ArrayList<String>();
		for(Collection c : childrenCollections) {
			childrenCollectionsCode.add(c.getCode());
		}
		parentCollectionCode = parentCollection.getCode();
		
		
		if(postsSet != null) {
			List<PostDto> postDtos= new ArrayList<PostDto>();
			for(Post p : postsSet) {
				PostDto pd = new PostDto(p);
				postDtos.add(pd);
			}
			posts = postDtos;
		}
	}
	
	public List<PostDto> getPosts() {
		return posts;
	}

	public void setPosts(List<PostDto> posts) {
		this.posts = posts;
	}

	public List<String> getChildrenCollectionsCode() {
		return childrenCollectionsCode;
	}

	public void setChildrenCollectionsCode(List<String> childrenCollectionsCode) {
		this.childrenCollectionsCode = childrenCollectionsCode;
	}

	public String getParentCollectionCode() {
		return parentCollectionCode;
	}

	public void setParentCollectionCode(String parentCollectionCode) {
		this.parentCollectionCode = parentCollectionCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
