package org.meveo.api.dto.cpq;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;

/**
 * 
 * @author Mbarek-Ay
 * @version 10.0
 */ 
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductVersionDto extends BaseEntityDto {
	
	 /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7824004884683019697L;  

    /** The shortDescription. */
    @XmlAttribute()
    @NotNull
    private String shortDescription;

    /** The product code. */
    @NotNull
    @XmlElement(required = true)
    private String productCode;

    /** The currentVersion. */
    private int currentVersion;

    /** The status. */
    private VersionStatusEnum status;

    /** The statusDate. */
    private Date statusDate;

    /** The longDescription */
    private String longDescription ;

    /** The startDate */
    private Date startDate;

    /** The endDate */
    private Date endDate;
  
    
    /**
     * Instantiates a new product version dto.
     */
    public ProductVersionDto() {

    }

    /**
     * Instantiates a new product dto.
     *
     * @param productChargeInstance the ProductChargeInstance entity
     * @param customFieldInstances the custom field instances
     */
 
    

    public ProductVersionDto(ProductVersion productVersion) {  
		this.shortDescription = productVersion.getShortDescription();
		this.productCode = productVersion.getProduct().getCode();
		this.currentVersion = productVersion.getCurrentVersion();
		this.status = productVersion.getStatus();
		this.statusDate = productVersion.getStatusDate();
		this.longDescription =productVersion.getLongDescription();
		this.startDate = productVersion.getStartDate();
		this.endDate = productVersion.getEndDate();
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the currentVersion
	 */
	public int getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	/**
	 * @return the status
	 */
	public VersionStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(VersionStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "ProductVersionDto [shortDescription=" + shortDescription + ", productCode=" + productCode
				+ ", currentVersion=" + currentVersion + ", status=" + status + ", statusDate=" + statusDate
				+ ", longDescription=" + longDescription + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	 
    
}
