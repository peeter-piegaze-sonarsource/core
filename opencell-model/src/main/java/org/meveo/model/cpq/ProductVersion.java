package org.meveo.model.cpq;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 */
@Entity
@Table(name = "cpq_product_version")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_version_seq"), })
@NamedQueries({ @NamedQuery(name = "ProductVersion.findByProductAndVersion", 
query = "SELECT pv FROM ProductVersion pv left join  bv.product p where p.code=:productCode and pv.currentVersion=:currentVersion")})
public class ProductVersion extends BaseEntity{


	private static final long serialVersionUID = 1L;
	 
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_code", nullable = false, referencedColumnName = "id")
	@NotNull
    private Product product;
    
    /**
     * version of the product<br />
     * this value is auto increment, do not use its method setVersion
     */
    @Column(name = "version", nullable = false)
    @Min(1)
    private int currentVersion;
    
    /**
     * status . it can be DRAFT / PUBLIED / CLOSED  
     */
    @Column(name = "status", nullable = false)
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private VersionStatusEnum status;
    
    /**
     * date of status : it set automatically when ever the status of product is changed
     */
    @Column(name = "status_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull    
    private Date statusDate;
    
    /**
     * short description. must not be null
     */
    @Column(name = "short_description", nullable = false)
    @Lob
    @NotNull
    private String shortDescription;
    
    /**
     * long description
     */
    @Column(name = "long_description")
    @Lob
    private String longDescription;
    
    /**
     * start date 
     */
    @Column(name = "startDate")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /**
     * date end 
     */
    @Column(name = "endDate")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    /*
     * list of tag attached to this version
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_version_id")
    private Set<Tag> tagList = new HashSet<>();
    
    
    

	public void setId(Long id) {
		this.id = id;
	}


	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public VersionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(VersionStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Set<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(Set<Tag> tagList) {
		this.tagList = tagList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(endDate, id, longDescription, product, shortDescription, startDate, status, statusDate,
				tagList, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductVersion other = (ProductVersion) obj;
		return Objects.equals(endDate, other.endDate) && Objects.equals(id, other.id)
				&& Objects.equals(longDescription, other.longDescription) && Objects.equals(product, other.product)
				&& Objects.equals(shortDescription, other.shortDescription)
				&& Objects.equals(startDate, other.startDate) && status == other.status
				&& Objects.equals(statusDate, other.statusDate) && Objects.equals(tagList, other.tagList)
				&& version == other.version;
	}

	
	
}
