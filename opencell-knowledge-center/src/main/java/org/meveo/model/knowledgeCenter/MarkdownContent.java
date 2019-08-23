package org.meveo.model.knowledgeCenter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.bi.Job;
import org.meveo.model.billing.Language;

/**
 * Knowledge center Collection
 * 
 * @author Franck Valot
 */

@Entity
@ExportIdentifier({ "code" })
@Table(name = "kc_markdown_content")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kc_markdown_content_seq"), })
public class MarkdownContent extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6391915861326309872L;

	/**
     * content name (title)
     */
    @Column(name = "name", length = 255)
    @Size(max = 255)
    private String name;
    
    /**
     * Content
     */
    @Column(name = "content", length = 5000)
    @Size(max = 5000)
    private String content;
    
    /**
     * Language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    
	public MarkdownContent() {
	}
	

	public MarkdownContent(String name, String content, Language language) {
		this.name = name;
		this.content = content;
		this.language = language;
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

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
    
    
}
