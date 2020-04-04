/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PreInvoicingReportsDTO implements Serializable {

    private static final long serialVersionUID = 1043179691827388660L;
    private String billingCycleCode;
    private Integer billingAccountNumber;
    private Integer billableBillingAccountNumber;
    private Date lastTransactionDate;
    private Date invoiceDate;
    private BigDecimal amoutWitountTax;
    private Integer checkBANumber;
    private Integer directDebitBANumber;
    private Integer tipBANumber;
    private Integer wiretransferBANumber;
    private Integer creditDebitCardBANumber;

    private Integer checkBillableBANumber;
    private Integer directDebitBillableBANumber;
    private Integer tipBillableBANumber;
    private Integer wiretransferBillableBANumber;
    private Integer creditDebitCardBillableBANumber;

    private BigDecimal checkBillableBAAmountHT;
    private BigDecimal directDebitBillableBAAmountHT;
    private BigDecimal tipBillableBAAmountHT;
    private BigDecimal wiretransferBillableBAAmountHT;
    private BigDecimal creditDebitCardBillableBAAmountHT;
    // List<InvoiceSubCategory> InvoiceSubCategories = new ArrayList<InvoiceSubCategory>();

    private BigDecimal SubCategoriesAmountHT = new BigDecimal(0);
    private BigDecimal TaxesAmount = new BigDecimal(0);

    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    public Integer getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(Integer billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getBillableBillingAccountNumber() {
        return billableBillingAccountNumber;
    }

    public void setBillableBillingAccountNumber(Integer billableBillingAccountNumber) {
        this.billableBillingAccountNumber = billableBillingAccountNumber;
    }

    public BigDecimal getAmoutWitountTax() {
        return amoutWitountTax;
    }

    public void setAmoutWitountTax(BigDecimal amoutWitountTax) {
        this.amoutWitountTax = amoutWitountTax;
    }

    public Integer getCheckBANumber() {
        return checkBANumber;
    }

    public void setCheckBANumber(Integer checkBANumber) {
        this.checkBANumber = checkBANumber;
    }

    public Integer getDirectDebitBANumber() {
        return directDebitBANumber;
    }

    public void setDirectDebitBANumber(Integer directDebitBANumber) {
        this.directDebitBANumber = directDebitBANumber;
    }

    public Integer getTipBANumber() {
        return tipBANumber;
    }

    public void setTipBANumber(Integer tipBANumber) {
        this.tipBANumber = tipBANumber;
    }

    public Integer getWiretransferBANumber() {
        return wiretransferBANumber;
    }

    public void setWiretransferBANumber(Integer wiretransferBANumber) {
        this.wiretransferBANumber = wiretransferBANumber;
    }

    public Integer getCheckBillableBANumber() {
        return checkBillableBANumber;
    }

    public void setCheckBillableBANumber(Integer checkBillableBANumber) {
        this.checkBillableBANumber = checkBillableBANumber;
    }

    public Integer getDirectDebitBillableBANumber() {
        return directDebitBillableBANumber;
    }

    public void setDirectDebitBillableBANumber(Integer directDebitBillableBANumber) {
        this.directDebitBillableBANumber = directDebitBillableBANumber;
    }

    public Integer getTipBillableBANumber() {
        return tipBillableBANumber;
    }

    public void setTipBillableBANumber(Integer tipBillableBANumber) {
        this.tipBillableBANumber = tipBillableBANumber;
    }

    public Integer getWiretransferBillableBANumber() {
        return wiretransferBillableBANumber;
    }

    public void setWiretransferBillableBANumber(Integer wiretransferBillableBANumber) {
        this.wiretransferBillableBANumber = wiretransferBillableBANumber;
    }

    public BigDecimal getCheckBillableBAAmountHT() {
        return checkBillableBAAmountHT;
    }

    public void setCheckBillableBAAmountHT(BigDecimal checkBillableBAAmountHT) {
        this.checkBillableBAAmountHT = checkBillableBAAmountHT;
    }

    public BigDecimal getDirectDebitBillableBAAmountHT() {
        return directDebitBillableBAAmountHT;
    }

    public void setDirectDebitBillableBAAmountHT(BigDecimal directDebitBillableBAAmountHT) {
        this.directDebitBillableBAAmountHT = directDebitBillableBAAmountHT;
    }

    public BigDecimal getTipBillableBAAmountHT() {
        return tipBillableBAAmountHT;
    }

    public void setTipBillableBAAmountHT(BigDecimal tipBillableBAAmountHT) {
        this.tipBillableBAAmountHT = tipBillableBAAmountHT;
    }

    public BigDecimal getWiretransferBillableBAAmountHT() {
        return wiretransferBillableBAAmountHT;
    }

    public void setWiretransferBillableBAAmountHT(BigDecimal wiretransferBillableBAAmountHT) {
        this.wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT;
    }

    public BigDecimal getSubCategoriesAmountHT() {
        return SubCategoriesAmountHT;
    }

    public void setSubCategoriesAmountHT(BigDecimal subCategoriesAmountHT) {
        SubCategoriesAmountHT = subCategoriesAmountHT;
    }

    public BigDecimal getTaxesAmount() {
        return TaxesAmount;
    }

    public void setTaxesAmount(BigDecimal taxesAmount) {
        TaxesAmount = taxesAmount;
    }

    public Integer getCreditDebitCardBANumber() {
        return creditDebitCardBANumber;
    }

    public void setCreditDebitCardBANumber(Integer creditDebitCardBANumber) {
        this.creditDebitCardBANumber = creditDebitCardBANumber;
    }

    public Integer getCreditDebitCardBillableBANumber() {
        return creditDebitCardBillableBANumber;
    }

    public void setCreditDebitCardBillableBANumber(Integer creditDebitCardBillableBANumber) {
        this.creditDebitCardBillableBANumber = creditDebitCardBillableBANumber;
    }

    public BigDecimal getCreditDebitCardBillableBAAmountHT() {
        return creditDebitCardBillableBAAmountHT;
    }

    public void setCreditDebitCardBillableBAAmountHT(BigDecimal creditDebitCardBillableBAAmountHT) {
        this.creditDebitCardBillableBAAmountHT = creditDebitCardBillableBAAmountHT;
    }

}
