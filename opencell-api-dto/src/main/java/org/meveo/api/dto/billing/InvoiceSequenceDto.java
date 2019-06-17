package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Class InvoiceSequenceDto.
 * 
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "InvoiceSequence")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSequenceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sequence size. */
    private Integer sequenceSize;

    /** The current invoice nb. */
    private Long currentInvoiceNb;
    
    private List<InvoiceTypeDto> invoiceTypes;


    /**
     * Instantiates a new invoice type dto.
     */
    public InvoiceSequenceDto() {
        this.invoiceTypes = new ArrayList<>();
    }

    /**
     * Instantiates a new invoice sequence dto.
     *
     * @param invoiceSequence the invoice sequence
     */
    public InvoiceSequenceDto(InvoiceSequence invoiceSequence) {
        super(invoiceSequence);
        this.sequenceSize = invoiceSequence.getSequenceSize();
        this.currentInvoiceNb = invoiceSequence.getCurrentInvoiceNb();
        this.invoiceTypes =
                Optional.ofNullable(invoiceSequence.getInvoiceTypes())
                        .map(it -> it.stream()
                                .map(i -> new InvoiceTypeDto(i, null))
                                .collect(Collectors.toList())).orElse(null);
    }

    public Integer getSequenceSize() {
		return sequenceSize;
	}

	public void setSequenceSize(Integer sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}
    
    public List<InvoiceTypeDto> getInvoiceTypes() {
        return invoiceTypes;
    }
    
    public void setInvoiceTypes(List<InvoiceTypeDto> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
    }
    
    @Override
    public String toString() {
        return "InvoiceSequenceDto [code=" + getCode() + ", description=" + getDescription() + ", sequenceSize=" + getSequenceSize() + ", sequenceSize=" + getSequenceSize() + "]";
    }
}