package org.meveo.model.billing;


import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "billing_invoice_line")
public class InvoiceLine extends BusinessEntity {

    @OneToOne
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    @OneToOne
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    @OneToOne
    @JoinColumn(name = "billing_account_id")
    @NotNull
    private BillingAccount billingAccount;

    @OneToOne
    @JoinColumn(name = "accounting_article_id")
    @NotNull
    private AccountingArticle accountingArticle;

    @Column(name = "label")
    @NotNull
    private String label;

    @Column(name = "quantity")
    @NotNull
    private Long quantity;

    @Column(name = "unitPrice")
    @NotNull
    private BigDecimal unitPrice;

    @Column(name = "discount_rate")
    @NotNull
    private BigDecimal discountRate;

    @Column(name = "discount_amount_without_tax")
    @NotNull
    private BigDecimal discountAmountWithoutTax;

    @Column(name = "amount_without_tax")
    @NotNull
    private BigDecimal amountWithoutTax;

    @Column(name = "taxRate")
    @NotNull
    private BigDecimal taxRate;

    @Column(name = "amount_tax")
    @NotNull
    private BigDecimal amountTax;

    @Column(name = "amount_with_tax")
    @NotNull
    private BigDecimal amountWithTax;

    @Column(name = "prestation")
    private String prestation;

    @OneToOne
    @JoinColumn(name = "offer_Template_id")
    private OfferTemplate offer;

    @OneToOne
    @JoinColumn(name = "product_instance_id")
    private ProductInstance product;

    @OneToOne
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance service;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToOne
    @JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;

    @OneToOne
    @JoinColumn(name = "tax_id")
    private Tax tax;

    @Column(name = "order_number")
    private String orderNumber;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne
    @JoinColumn(name = "access_point_id")
    private Access accessPoint;

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountAmountWithoutTax() {
        return discountAmountWithoutTax;
    }

    public void setDiscountAmountWithoutTax(BigDecimal discountAmountWithoutTax) {
        this.discountAmountWithoutTax = discountAmountWithoutTax;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getPrestation() {
        return prestation;
    }

    public void setPrestation(String prestation) {
        this.prestation = prestation;
    }

    public OfferTemplate getOffer() {
        return offer;
    }

    public void setOffer(OfferTemplate offer) {
        this.offer = offer;
    }

    public ProductInstance getProduct() {
        return product;
    }

    public void setProduct(ProductInstance product) {
        this.product = product;
    }

    public ServiceInstance getService() {
        return service;
    }

    public void setService(ServiceInstance service) {
        this.service = service;
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

    public DiscountPlan getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Access getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(Access accessPoint) {
        this.accessPoint = accessPoint;
    }
}
