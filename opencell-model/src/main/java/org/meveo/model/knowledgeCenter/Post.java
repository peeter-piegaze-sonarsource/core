/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.knowledgeCenter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Table(name = "kc_post")
@DiscriminatorValue(value = "ACCT_CTACT")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kc_post_seq"), })
public class Post extends BusinessEntity {

    private static final long serialVersionUID = 3772773449495155646L;
    
    /**
     * Content
     */
    @Column(name = "name", length = 255)
    @Size(max = 255)
    private String name;
    
    /**
     * Content
     */
    @Column(name = "content", length = 2000)
    @Size(max = 2000)
    private String content;
    
    /**
     * Comments in the post
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Comment> commments = new HashSet<Comment>();

    /**
     * Collection
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    /**
     * Tags
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kc_post_tag", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<String>();

    public Post() {
        
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

	public Set<Comment> getCommments() {
		return commments;
	}

	public void setCommments(Set<Comment> commments) {
		this.commments = commments;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
    
}