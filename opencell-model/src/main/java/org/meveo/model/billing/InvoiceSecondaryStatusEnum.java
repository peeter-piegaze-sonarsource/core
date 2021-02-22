package org.meveo.model.billing;

import static java.util.Arrays.stream;

import java.util.NoSuchElementException;

public enum InvoiceSecondaryStatusEnum {

    HAS_TAXES(1, "InvoiceSecondaryStatusEnum.hasTaxes"),
    HAS_DISCOUNTS(2, "InvoiceSecondaryStatusEnum.hasDiscounts"),
    HAS_MINIMUM(3, "InvoiceSecondaryStatusEnum.hasMinimum");

    private Integer id;
    private String label;

    InvoiceSecondaryStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets enum by its id.
     * 
     * @param id of invoice secondary status
     * @return invoice secondary status enum
     */
    public static InvoiceSecondaryStatusEnum getValue(Integer id) {
        return stream(InvoiceSecondaryStatusEnum.values())
                    .filter(status -> status.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No status with given ID : " + id));
    }
}