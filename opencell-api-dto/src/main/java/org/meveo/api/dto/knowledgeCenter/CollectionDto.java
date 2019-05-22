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

	private List<CollectionDto> children;
	
	private String parentCode;
	
	private List<String> postsCode;

	private String name;
	
	public CollectionDto() {
		
	}
	
	public CollectionDto(Collection collection) {
		super(collection);
		name = collection.getName();
		Collection parent = collection.getParentCollection();
		Set<Collection> childrenCollections = collection.getChildrenCollections();
		Set<Post> posts = collection.getPosts();
		

		if(parent != null)
			parentCode = parent.getCode();
		
		if(childrenCollections != null) {
			children = new ArrayList<CollectionDto>();
			for(Collection c : childrenCollections) {
				children.add(new CollectionDto(c));
			}
		}
		
		
		if(posts != null) {
			postsCode = new ArrayList<String>();
			for(Post p : posts) {
				postsCode.add(p.getCode());
			}
		}
	}

	public List<CollectionDto> getChildren() {
		return children;
	}

	public void setChildren(List<CollectionDto> children) {
		this.children = children;
	}

	
	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public List<String> getPostsCode() {
		return postsCode;
	}

	public void setPostsCode(List<String> postsCode) {
		this.postsCode = postsCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
