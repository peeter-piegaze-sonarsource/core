package org.meveo.api.dto.knowledgeCenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.model.knowledgeCenter.MarkdownContent;
import org.meveo.model.knowledgeCenter.Post;

public class CollectionDto extends BusinessEntityDto {

	class Posts {
		private Set<MarkdownContentDto> data = new HashSet<MarkdownContentDto>();
		private String code;
		
		public Posts(Set<MarkdownContentDto> data, String code) {
			this.data = data;
			this.code = code;
			for(MarkdownContentDto mdc : this.data) {
				mdc.setContent(null);
			}
		}
		

		public Set<MarkdownContentDto> getData() {
			return data;
		}


		public void setData(Set<MarkdownContentDto> data) {
			this.data = data;
		}


		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4889360284187982497L;

	private List<CollectionDto> children;
	private String parentCode;
	private List<Posts> posts;
	
	private List<MarkdownContentDto> data = new ArrayList<MarkdownContentDto>();
	private String name;
	private String content;
	private String language;
	
	public CollectionDto() {
		
	}
	
	public CollectionDto(Collection collection) {
		super(collection);
		Set<MarkdownContent> markdownContents = collection.getMarkdownContents();
		for(MarkdownContent mdc : markdownContents) {
			data.add(new MarkdownContentDto(mdc));
		}
		Collection parent = collection.getParentCollection();
		Set<Collection> childrenCollections = collection.getChildrenCollections();
		Set<Post> postsdata = collection.getPosts();
		

		if(parent != null)
			parentCode = parent.getCode();
		
		if(childrenCollections != null) {
			children = new ArrayList<CollectionDto>();
			for(Collection c : childrenCollections) {
				children.add(new CollectionDto(c));
			}
		}
		
		
		if(postsdata != null) {
			posts = new ArrayList<Posts>();
			for(Post p : postsdata) {
				Set<MarkdownContentDto> mdcdtos = new HashSet<MarkdownContentDto>();
				for(MarkdownContent mdc : p.getMarkdownContents()) {
					mdcdtos.add(new MarkdownContentDto(mdc));
				}
				posts.add(new Posts(mdcdtos, p.getCode()));
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

	public List<Posts> getPosts() {
		return posts;
	}

	public void setPosts(List<Posts> posts) {
		this.posts = posts;
	}

	public List<MarkdownContentDto> getData() {
		return data;
	}

	public void setData(List<MarkdownContentDto> data) {
		this.data = data;
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

