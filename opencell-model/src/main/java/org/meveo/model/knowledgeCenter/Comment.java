package org.meveo.model.knowledgeCenter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Knowledge Center / Post
 * 
 * @author Franck Valot
 */
@Entity
@ExportIdentifier({ "code" })
@Table(name = "kc_comment")
@DiscriminatorValue(value = "ACCT_CTACT")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kc_comment_seq"), })
public class Comment extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7170050892148515342L;
	
	/**
     * Content
     */
    @Column(name = "content", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String content;
    
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

	public Comment() {
        
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}
