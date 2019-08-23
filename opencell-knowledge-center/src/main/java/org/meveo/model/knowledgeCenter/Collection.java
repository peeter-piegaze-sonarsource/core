package org.meveo.model.knowledgeCenter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Knowledge center Collection
 * 
 * @author Franck Valot
 */
@Entity
@ExportIdentifier({ "code" })
@Table(name = "kc_collection")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kc_collection_seq"), })
public class Collection extends BusinessEntity {


    /**
	 * 
	 */
	private static final long serialVersionUID = -5232430912460425525L;

	/**
     * Children Collections
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "parentCollection", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    private Set<Collection> childrenCollections = new HashSet<Collection>();
    
    /**
     * Parent Collection
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentcollection_id")
    private Collection parentCollection;
    
    /**
     * Posts in the collection
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Post> posts = new HashSet<Post>();
    
    /**
     * Collection content
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MarkdownContent> markdownContents = new HashSet<MarkdownContent>();

    public Collection() {

    }
    
    public Collection(String code) {
        this.setCode(code);
    }
    
    

	public Set<MarkdownContent> getMarkdownContents() {
		return markdownContents;
	}

	public void setMarkdownContents(Set<MarkdownContent> markdownContents) {
		this.markdownContents = markdownContents;
	}

	public Set<Collection> getChildrenCollections() {
		return childrenCollections;
	}



	public void setChildrenCollections(Set<Collection> childrenCollections) {
		this.childrenCollections = childrenCollections;
	}



	public Collection getParentCollection() {
		return parentCollection;
	}



	public void setParentCollection(Collection parentCollection) {
		this.parentCollection = parentCollection;
	}


	public Set<Post> getPosts() {
		return posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}   
}
