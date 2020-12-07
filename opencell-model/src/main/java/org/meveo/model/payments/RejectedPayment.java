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
package org.meveo.model.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue(value = "R")
public class RejectedPayment extends AccountOperation {

    private static final long serialVersionUID = 1L;

    @Column(name = "rejected_type")
    @Enumerated(EnumType.STRING)
    private RejectedType rejectedType;

    @Column(name = "rejected_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rejectedDate;

    @Column(name = "rejected_description", length = 255)
    @Size(max = 255)
    private String rejectedDescription;

    @Column(name = "rejected_code", length = 255)
    @Size(max = 255)
    private String rejectedCode;

    /**
     * Bank LOT number
     */
    @Column(name = "bank_lot", columnDefinition = "text")
    private String bankLot;

    /**
     * Bank reference
     */
    @Column(name = "bank_reference", length = 255)
    @Size(max = 255)
    private String bankReference;

    @OneToMany(mappedBy = "rejectedPayment")
    List<AccountOperation> listAaccountOperationSupposedPaid = new ArrayList<AccountOperation>();

    private PaymentMethodEnum paymentMethod;

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getRejectedDescription() {
        return rejectedDescription;
    }

    public void setRejectedDescription(String rejectedDescription) {
        this.rejectedDescription = rejectedDescription;
    }

    public String getRejectedCode() {
        return rejectedCode;
    }

    public void setRejectedCode(String rejectedCode) {
        this.rejectedCode = rejectedCode;
    }

    public RejectedType getRejectedType() {
        return rejectedType;
    }

    public void setRejectedType(RejectedType rejectedType) {
        this.rejectedType = rejectedType;
    }

    public String getBankLot() {
        return bankLot;
    }

    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    /**
     * @return the listAaccountOperationSupposedPaid
     */
    public List<AccountOperation> getListAaccountOperationSupposedPaid() {
        return listAaccountOperationSupposedPaid;
    }

    /**
     * @param listAaccountOperationSupposedPaid the listAaccountOperationSupposedPaid to set
     */
    public void setListAaccountOperationSupposedPaid(List<AccountOperation> listAaccountOperationSupposedPaid) {
        this.listAaccountOperationSupposedPaid = listAaccountOperationSupposedPaid;
    }

    /**
     * get paymentMethod
     * @return paymentMethod
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * set paymentMethod
     * @param paymentMethod paymentMethod
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
