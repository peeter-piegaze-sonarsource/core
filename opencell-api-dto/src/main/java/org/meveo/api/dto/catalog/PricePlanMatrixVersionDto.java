package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixVersionDto extends BaseEntityDto {

    /**
     *
     */
    private static final long serialVersionUID = 1105680934764861643L;

    private Long id; 
    
    @NotNull
    private String pricePlanMatrixCode;

    private VersionStatusEnum statusEnum;
    protected int version;
    private Date statusDate;
    private String label;
    protected DatePeriod validity = new DatePeriod();
    @NotNull
    private Boolean isMatrix;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;
    private String amountWithoutTaxEL;
    private String amountWithTaxEL;
    protected int priority=0;

    private Set<PricePlanMatrixColumnDto> columns;

    private Set<PricePlanMatrixLineDto> lines;

    public PricePlanMatrixVersionDto() {
    }

    public PricePlanMatrixVersionDto(PricePlanMatrixVersion pricePlanMatrixVersion) {
    	this.id = pricePlanMatrixVersion.getId();
        setLabel(pricePlanMatrixVersion.getLabel());
        setMatrix(pricePlanMatrixVersion.isMatrix());
        setPricePlanMatrixCode(pricePlanMatrixVersion.getPricePlanMatrix().getCode());
        setVersion(pricePlanMatrixVersion.getCurrentVersion());
        setStatusEnum(pricePlanMatrixVersion.getStatus());
        setStatusDate(pricePlanMatrixVersion.getStatusDate());
        setValidity(pricePlanMatrixVersion.getValidity());
        setAmountWithoutTax(pricePlanMatrixVersion.getAmountWithoutTax());
        setAmountWithTax(pricePlanMatrixVersion.getAmountWithTax());
        setAmountWithoutTaxEL(pricePlanMatrixVersion.getAmountWithoutTaxEL());
        setAmountWithTaxEL(pricePlanMatrixVersion.getAmountWithTaxEL());
        setPriority(pricePlanMatrixVersion.getPriority());
        if (pricePlanMatrixVersion.getLines() != null && !pricePlanMatrixVersion.getLines().isEmpty())
            lines = pricePlanMatrixVersion.getLines().stream()
                    .map(PricePlanMatrixLineDto::new)
                    .collect(Collectors.toSet());
        if (pricePlanMatrixVersion.getColumns() != null && !pricePlanMatrixVersion.getColumns().isEmpty()) {
            columns = pricePlanMatrixVersion.getColumns().stream()
                    .map(PricePlanMatrixColumnDto::new)
                    .collect(Collectors.toSet());
        }
    }

    public String getPricePlanMatrixCode() {
        return pricePlanMatrixCode;
    }

    public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
        this.pricePlanMatrixCode = pricePlanMatrixCode;
    }

    public VersionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(VersionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DatePeriod getValidity() {
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public Boolean getMatrix() {
        return isMatrix;
    }

    public void setMatrix(Boolean matrix) {
        isMatrix = matrix;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    public Set<PricePlanMatrixLineDto> getLines() {
        return lines == null ? new HashSet<>() : lines;
    }

    public void setLines(Set<PricePlanMatrixLineDto> lines) {
        this.lines = lines;
    }

    public Set<PricePlanMatrixColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(Set<PricePlanMatrixColumnDto> columns) {
        this.columns = columns;
    }

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
    
    
}
