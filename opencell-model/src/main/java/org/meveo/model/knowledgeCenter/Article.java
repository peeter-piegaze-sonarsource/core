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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;	

/**
 * Knowledge center Collection
 * 
 * @author Franck Valot
 */
@Entity
@ExportIdentifier({ "id" })
@Table(name = "kc_article")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kc_article_seq"), })
public class Article extends AuditableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3056966851045146206L;
	
	/**
     * Children Articles
     */
    @OneToMany(mappedBy = "parentArticle", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    private Set<Article> childrenArticle = new HashSet<Article>();
	
	 /**
     * Parent Article
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_article_id")
    private Article parentArticle;
    
    /**
     * Content of Article
     */
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Content> contents = new HashSet<Content>();

    
    
	public Article() {

	}

	public Set<Article> getChildrenArticle() {
		return childrenArticle;
	}

	public void setChildrenArticle(Set<Article> childrenArticle) {
		this.childrenArticle = childrenArticle;
	}

	public Article getParentArticle() {
		return parentArticle;
	}

	public void setParentArticle(Article parentArticle) {
		this.parentArticle = parentArticle;
	}

	public Set<Content> getContents() {
		return contents;
	}

	public void setContents(Set<Content> contents) {
		this.contents = contents;
	}
    
    
}
