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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.rating.EDR;

/**
 * Consumption operation
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@Table(name = "billing_wallet_operation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_wallet_operation_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("W")
@NamedQueries({
        @NamedQuery(name = "WalletOperation.getWalletOperationsBilled", query = "SELECT o.id FROM WalletOperation o join o.ratedTransaction rt WHERE rt.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED AND o.id IN :walletIdList"),
        @NamedQuery(name = "WalletOperation.listByRatedTransactionId", query = "SELECT o FROM WalletOperation o WHERE o.status='TREATED' and o.ratedTransaction.id=:ratedTransactionId"),

        @NamedQuery(name = "WalletOperation.listByBRId", query = "SELECT o FROM WalletOperation o WHERE o.status='TREATED' and o.ratedTransaction.billingRun.id=:brId"),

        @NamedQuery(name = "WalletOperation.listToRateIds", query = "SELECT o.id FROM WalletOperation o WHERE o.status='OPEN' and (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate )"),
        @NamedQuery(name = "WalletOperation.listToRateByBA", query = "SELECT o FROM WalletOperation o WHERE o.status='OPEN' and (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) AND o.wallet.userAccount.billingAccount=:billingAccount"),
        @NamedQuery(name = "WalletOperation.listToRateBySubscription", query = "SELECT o FROM WalletOperation o WHERE o.status='OPEN' and (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) AND o.subscription=:subscription"),
        @NamedQuery(name = "WalletOperation.listToRateByOrderNumber", query = "SELECT o FROM WalletOperation o WHERE o.status='OPEN' and (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) AND o.orderNumber=:orderNumber"),

        @NamedQuery(name = "WalletOperation.listToRerate", query = "SELECT o.id FROM WalletOperation o WHERE o.status='TO_RERATE'"),

        @NamedQuery(name = "WalletOperation.getBalancesForWalletInstance", query = "SELECT sum(case when o.status in ('OPEN','TREATED') then o.amountWithTax else 0 end), sum(o.amountWithTax) FROM WalletOperation o WHERE o.wallet.id=:walletId and o.status in ('OPEN','RESERVED','TREATED')"),
        @NamedQuery(name = "WalletOperation.getBalancesForCache", query = "SELECT o.wallet.id, sum(case when o.status in ('OPEN','TREATED') then o.amountWithTax else 0 end), sum(o.amountWithTax) FROM WalletOperation o WHERE o.status in ('OPEN','RESERVED','TREATED') and o.wallet.walletTemplate.walletType='PREPAID' group by o.wallet.id"),

        @NamedQuery(name = "WalletOperation.getOpenByWallet", query = "SELECT o FROM WalletOperation o WHERE o.status='OPEN' and o.wallet=:wallet"),

        @NamedQuery(name = "WalletOperation.setStatusToRerate", query = "UPDATE WalletOperation o SET o.status='TO_RERATE', o.updated = :now "
                + " WHERE o.status='TREATED' AND o.ratedTransaction.id IN (SELECT o1.ratedTransaction.id FROM WalletOperation o1 WHERE o1.status='TREATED' and o1.id IN :notBilledWalletIdList)"),

        @NamedQuery(name = "WalletOperation.setStatusToCanceled", query = "UPDATE WalletOperation o SET o.status='CANCELED', o.updated = :now where o.status<>'TREATED' and o.chargeInstance=:chargeInstance"),

        @NamedQuery(name = "WalletOperation.deleteScheduled", query = "DELETE WalletOperation o WHERE o.chargeInstance=:chargeInstance AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.SCHEDULED"),

        @NamedQuery(name = "WalletOperation.findByUAAndCode", query = "SELECT o FROM WalletOperation o WHERE o.wallet.userAccount=:userAccount and o.code=:code"),

        @NamedQuery(name = "WalletOperation.countNotTreatedByBA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> 'TREATED' AND o.wallet.userAccount.billingAccount=:billingAccount"),
        @NamedQuery(name = "WalletOperation.countNotTreatedByUA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> 'TREATED' AND o.wallet.userAccount=:userAccount"),
        @NamedQuery(name = "WalletOperation.countNotTreatedByCA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> 'TREATED' AND o.wallet.userAccount.billingAccount.customerAccount=:customerAccount"),

        @NamedQuery(name = "WalletOperation.countNbrWalletsOperationByStatus", query = "select o.status, count(o.id) from WalletOperation o group by o.status"),

        @NamedQuery(name = "WalletOperation.listNotOpenedWObetweenTwoDates", query = "SELECT o FROM WalletOperation o WHERE o.status != 'OPEN' AND :firstTransactionDate<o.operationDate AND o.operationDate<:lastTransactionDate and o.id >:lastId order by o.id asc"),
        @NamedQuery(name = "WalletOperation.listWObetweenTwoDatesByStatus", query = "SELECT o FROM WalletOperation o WHERE o.status in (:status) AND :firstTransactionDate<=o.operationDate AND o.operationDate<=:lastTransactionDate and o.id >:lastId order by o.id asc"),
        @NamedQuery(name = "WalletOperation.deleteNotOpenWObetweenTwoDates", query = "delete FROM WalletOperation o WHERE o.status<>'OPEN' AND :firstTransactionDate<o.operationDate AND o.operationDate<:lastTransactionDate"),
        @NamedQuery(name = "WalletOperation.deleteWObetweenTwoDatesByStatus", query = "delete FROM WalletOperation o WHERE o.status in (:status) AND :firstTransactionDate<=o.operationDate AND o.operationDate<=:lastTransactionDate")})
public class WalletOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Operation code - corresponds in majority of cases to charge code
     */
    @Column(name = "code", length = 255)
    @Size(max = 255)
    private String code;

    /**
     * Description - corresponds in majority of cases to charge description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Last status change timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    /**
     * The wallet on which the operation is applied.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /**
     * Operation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "operation_date")
    private Date operationDate;

    /**
     * Invoicing date if invoice date should be in a future and does not match the billing cycle invoicing dates
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invoicing_date")
    private Date invoicingDate;

    /**
     * Operation type Credit/Debit
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_debit_flag")
    private OperationTypeEnum type;

    /**
     * Associated charge instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_instance_id", nullable = false)
    @NotNull
    private ChargeInstance chargeInstance;

    /**
     * Currency of operation rated amounts
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    /**
     * Tax applied
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_id", nullable = false)
    @NotNull
    private Tax tax;

    /**
     * Tax percent applied
     */
    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal taxPercent;

    /**
     * Unit price without tax
     */
    @Column(name = "unit_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithoutTax;

    /**
     * Unit price with tax
     */
    @Column(name = "unit_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithTax;

    /**
     * Unit price tax amount
     */
    @Column(name = "unit_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountTax;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity;

    /**
     * Total amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    /**
     * Total amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    /**
     * Total tax amount
     */
    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    /**
     * Counter instance to track consumption
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_id")
    private CounterInstance counter;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_1", length = 255)
    @Size(max = 255)
    private String parameter1;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_2", length = 255)
    @Size(max = 255)
    private String parameter2;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_3", length = 255)
    @Size(max = 255)
    private String parameter3;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_extra", columnDefinition = "TEXT")
    private String parameterExtra;

    /**
     * Operation start date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Operation end date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Service/charge subscription timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    /**
     * Offer code
     */
    @Column(name = "offer_code", length = 255)
    @Size(max = 255, min = 1)
    protected String offerCode;

    /**
     * Seller associated to operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    /**
     * Price plan applied during rating
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceplan_id")
    private PricePlanMatrix priceplan;

    /**
     * Rerated wallet operation
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    private WalletOperation reratedWalletOperation;

    /**
     * Input unit description
     */
    @Column(name = "input_unit_description", length = 20)
    @Size(max = 20)
    private String inputUnitDescription;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;


    /**
     * input_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_unitofmeasure")
    private UnitOfMeasure inputUnitOfMeasure;

    /**
     * rating_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_unitofmeasure")
    private UnitOfMeasure ratingUnitOfMeasure;

    /**
     * Input quantity
     */
    @Column(name = "input_quantity", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
    private BigDecimal inputQuantity;

    /**
     * EDR that produced this operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edr_id")
    private EDR edr;

    /**
     * Order number in cases when operation was originated from an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Raw rating amount without tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Deprecated
    @Column(name = "raw_amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithoutTax;

    /**
     * Raw rating amount with tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Deprecated
    @Column(name = "raw_amount_with_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithTax;

    /**
     * Associated Invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    /**
     * Associated Subscription when operation is tied to subscription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    protected Subscription subscription;

    /**
     * Service instance that Wallet operation is applied to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Billing account
     */
    @Transient
    private BillingAccount billingAccount;

    /**
     * Offer template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offerTemplate;

    /**
     * Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_transaction_id")
    protected RatedTransaction ratedTransaction;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WalletOperationStatusEnum status = WalletOperationStatusEnum.OPEN;

    /**
     * Constructor
     */
    public WalletOperation() {
    }

    /**
     * Constructor
     * 
     * @param chargeInstance Charge instance
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Quantity in charge units
     * @param operationDate Operation date
     * @param orderNumber Order number
     * @param criteria1 Criteria 1
     * @param criteria2 Criteria 2
     * @param criteria3 Criteria 3
     * @param criteriaExtra Criteria extra
     * @param tax Tax to apply
     * @param startDate Operation date range - start date
     * @param endDate Operation date range - end date
     */
    @SuppressWarnings("deprecation")
    public WalletOperation(ChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Date operationDate, String orderNumber, String criteria1,
            String criteria2, String criteria3, String criteriaExtra, Tax tax, Date startDate, Date endDate) {

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        this.code = chargeTemplate.getCode();
        this.description = chargeInstance.getDescription();
        this.chargeInstance = chargeInstance;
        UnitOfMeasure CTInputUnitOfMeasure = chargeTemplate.getInputUnitOfMeasure();
        UnitOfMeasure CTRatingUnitOfMeasure = chargeTemplate.getRatingUnitOfMeasure();
        this.ratingUnitDescription = CTRatingUnitOfMeasure != null ? CTRatingUnitOfMeasure.getCode() : chargeTemplate.getRatingUnitDescription();
        this.inputUnitDescription = CTInputUnitOfMeasure != null ? CTInputUnitOfMeasure.getCode() : chargeTemplate.getInputUnitDescription();
        this.inputUnitOfMeasure=CTInputUnitOfMeasure;
		this.ratingUnitOfMeasure=CTRatingUnitOfMeasure;
        this.operationDate = operationDate;
        this.orderNumber = orderNumber;
        this.parameter1 = criteria1;
        this.parameter2 = criteria2;
        this.parameter3 = criteria3;
        this.inputQuantity = inputQuantity;

        if (chargeInstance instanceof RecurringChargeInstance) {
            this.subscriptionDate = ((RecurringChargeInstance) chargeInstance).getSubscriptionDate();

        } else if (chargeInstance instanceof UsageChargeInstance) {
            this.subscriptionDate = chargeInstance.getSubscription().getSubscriptionDate();
            this.counter = ((UsageChargeInstance) chargeInstance).getCounter();

        } else if (chargeInstance instanceof OneShotChargeInstance) {
            if (chargeInstance.getServiceInstance() != null) {
                this.subscriptionDate = chargeInstance.getServiceInstance().getSubscriptionDate();
            } else if (chargeInstance.getSubscription() != null) {
                this.subscriptionDate = chargeInstance.getSubscription().getSubscriptionDate();
            }
        }

        if (quantityInChargeUnits != null) {
            this.quantity = quantityInChargeUnits;

        } else if (inputQuantity != null) {
            this.quantity = NumberUtils.getInChargeUnit(inputQuantity, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode());
        }

        UserAccount userAccount = chargeInstance.getUserAccount();

        if (chargeInstance.getInvoicingCalendar() != null && this.subscriptionDate != null) {
            chargeInstance.getInvoicingCalendar().setInitDate(this.subscriptionDate);

            this.invoicingDate = chargeInstance.getInvoicingCalendar().nextCalendarDate(operationDate);
        }

        this.seller = chargeInstance.getSeller();
        this.serviceInstance = chargeInstance.getServiceInstance();
        this.subscription = chargeInstance.getSubscription();
        this.currency = chargeInstance.getCurrency().getCurrency();

        if (this.seller == null && userAccount != null) {
            this.seller = userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }

        this.tax = tax;
        this.taxPercent = tax.getPercent();

        this.startDate = startDate;
        this.endDate = endDate;

        if (this.subscription != null) {
            this.offerTemplate = this.subscription.getOffer();
            this.offerCode = this.offerTemplate.getCode();
        }
        this.invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();

        // TODO:check that setting the principal wallet at this stage is correct
        this.wallet = userAccount.getWallet();
        this.subscription = chargeInstance.getSubscription();
        this.billingAccount = userAccount.getBillingAccount();

        this.status = WalletOperationStatusEnum.OPEN;
        this.updated = new Date();
    }

    /**
     * Constructor
     * 
     * @param code Code
     * @param description Charge description
     * @param wallet Wallet on which operation is performed
     * @param operationDate Operation date
     * @param invoicingDate Invoicing date
     * @param type Credit/Debit type
     * @param currency Currency
     * @param tax Tax applied
     * @param unitAmountWithoutTax Unit amount without tax
     * @param unitAmountWithTax Unit amount with tax
     * @param unitAmountTax Unit amount tax
     * @param quantity Rating quantity
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Amount tax
     * @param parameter1 Parameter 1
     * @param parameter2 Parameter 2
     * @param parameter3 Parameter 3
     * @param parameterExtra Extra parameter
     * @param startDate Operation date range - Start date
     * @param endDate Operation date range - End date
     * @param subscriptionDate Subscription date
     * @param offerTemplate Offer template
     * @param seller Seller
     * @param inputUnitDescription Input unit description
     * @param ratingUnitDescription Rating unit description
     * @param inputQuantity Input quantity
     * @param orderNumber Order number
     * @param invoiceSubCategory Invoice sub category
     * @param status Status
     */
    public WalletOperation(String code, String description, WalletInstance wallet, Date operationDate, Date invoicingDate, OperationTypeEnum type, Currency currency, Tax tax,
            BigDecimal unitAmountWithoutTax, BigDecimal unitAmountWithTax, BigDecimal unitAmountTax, BigDecimal quantity, BigDecimal amountWithoutTax, BigDecimal amountWithTax,
            BigDecimal amountTax, String parameter1, String parameter2, String parameter3, String parameterExtra, Date startDate, Date endDate, Date subscriptionDate,
            OfferTemplate offerTemplate, Seller seller, String inputUnitDescription, String ratingUnitDescription, BigDecimal inputQuantity, String orderNumber,
            InvoiceSubCategory invoiceSubCategory, WalletOperationStatusEnum status) {
        super();
        this.code = code;
        this.description = description;
        this.wallet = wallet;
        this.operationDate = operationDate;
        this.invoicingDate = invoicingDate;
        this.type = type;
        this.currency = currency;
        this.tax = tax;
        this.taxPercent = tax.getPercent();
        this.unitAmountWithoutTax = unitAmountWithoutTax;
        this.unitAmountWithTax = unitAmountWithTax;
        this.unitAmountTax = unitAmountTax;
        this.quantity = quantity;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.amountTax = amountTax;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.parameterExtra = parameterExtra;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subscriptionDate = subscriptionDate;
        this.seller = seller;
        this.inputUnitDescription = inputUnitDescription;
        this.ratingUnitDescription = ratingUnitDescription;
        this.inputQuantity = inputQuantity;
        this.orderNumber = orderNumber;
        this.invoiceSubCategory = invoiceSubCategory;

        if (chargeInstance != null) {
            this.serviceInstance = chargeInstance.getServiceInstance();
            this.subscription = chargeInstance.getSubscription();
            this.currency = chargeInstance.getCurrency().getCurrency();
        }
        this.offerTemplate = offerTemplate;
        if (offerTemplate != null) {
            this.offerCode = offerTemplate.getCode();
        }
        this.status = status != null ? status : WalletOperationStatusEnum.OPEN;
        this.updated = new Date();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public Date getInvoicingDate() {
        return invoicingDate;
    }

    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    public OperationTypeEnum getType() {
        return type;
    }

    public void setType(OperationTypeEnum type) {
        this.type = type;
    }

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public void setChargeInstance(ChargeInstance chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return Tax applied
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * @param tax Tax applied
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    /**
     * @return Tax percent applied
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent Tax percent applied
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public CounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CounterInstance counter) {
        this.counter = counter;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
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

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * @return Seller associated to wallet operation
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller associated to wallet operation
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public PricePlanMatrix getPriceplan() {
        return priceplan;
    }

    public void setPriceplan(PricePlanMatrix priceplan) {
        this.priceplan = priceplan;
    }

    public WalletOperation getReratedWalletOperation() {
        return reratedWalletOperation;
    }

    public void setReratedWalletOperation(WalletOperation reratedWalletOperation) {
        this.reratedWalletOperation = reratedWalletOperation;
    }

    public EDR getEdr() {
        return edr;
    }

    public void setEdr(EDR edr) {
        this.edr = edr;
    }

    @Transient
    public WalletOperation getUnratedClone() {
        WalletOperation result = new WalletOperation();
        return fillUnratedClone(result);
    }

    protected WalletOperation fillUnratedClone(WalletOperation result) {
        result.setBillingAccount(billingAccount);
        result.setChargeInstance(chargeInstance);
        result.setCode(code);
        result.setCounter(counter);
        result.setCurrency(currency);
        result.setDescription(description);
        result.setEndDate(endDate);
        result.setInvoiceSubCategory(invoiceSubCategory);
        result.setInvoicingDate(invoicingDate);
        result.setOfferCode(offerCode);
        result.setOfferTemplate(offerTemplate);
        result.setOperationDate(operationDate);
        result.setParameter1(parameter1);
        result.setParameter2(parameter2);
        result.setParameter3(parameter3);
        result.setParameterExtra(parameterExtra);
        result.setOrderNumber(orderNumber);
        result.setPriceplan(priceplan);
        result.setQuantity(quantity);
        result.setSeller(seller);
        result.setStartDate(startDate);
        result.setSubscriptionDate(subscriptionDate);
        result.setTax(tax);
        result.setTaxPercent(taxPercent);
        result.setType(type);
        result.setUnitAmountTax(unitAmountTax);
        result.setUnitAmountWithoutTax(unitAmountWithoutTax);
        result.setUnitAmountWithTax(unitAmountWithTax);
        result.setRatingUnitDescription(ratingUnitDescription);
        result.setInputQuantity(inputQuantity);
        result.setInputUnitDescription(inputUnitDescription);
        result.setWallet(wallet);
        result.setEdr(edr);
        result.setSubscription(subscription);
        result.setUpdated(updated);

        return result;
    }

    /**
     * @return Billing account associated to wallet operation
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account associated to wallet operation
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
        if (offerTemplate != null && offerCode == null) {
            offerCode = offerTemplate.getCode();
        }
    }

    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    public void setInputUnitDescription(String inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public BigDecimal getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(BigDecimal inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getRawAmountWithoutTax() {
        return rawAmountWithoutTax;
    }

    public void setRawAmountWithoutTax(BigDecimal rawAmountWithoutTax) {
        this.rawAmountWithoutTax = rawAmountWithoutTax;
    }

    public BigDecimal getRawAmountWithTax() {
        return rawAmountWithTax;
    }

    public void setRawAmountWithTax(BigDecimal rawAmountWithTax) {
        this.rawAmountWithTax = rawAmountWithTax;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Compute derived amounts amountWithoutTax/amountWithTax/amountTax unitAmountWithoutTax/unitAmountWithTax/unitAmountTax
     * 
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     */
    public void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingModeEnum roundingMode) {

        // Unit amount calculation is left with higher precision
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithTax, taxPercent, isEnterprise, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        unitAmountWithoutTax = amounts[0];
        unitAmountWithTax = amounts[1];
        unitAmountTax = amounts[2];

        amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxPercent, isEnterprise, rounding, roundingMode.getRoundingMode());
        amountWithoutTax = amounts[0];
        amountWithTax = amounts[1];
        amountTax = amounts[2];
    }

    /**
     * @return Service instance that Wallet operation is applied to
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * @param serviceInstance Service instance that Wallet operation is applied to
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * @return Rated transaction
     */
    public RatedTransaction getRatedTransaction() {
        return ratedTransaction;
    }

    /**
     * @param ratedTransaction Rated transaction
     */
    public void setRatedTransaction(RatedTransaction ratedTransaction) {
        this.ratedTransaction = ratedTransaction;
    }

    /**
     * @return Processing status
     */
    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

    /**
     * Change status and update a last updated timestamp
     * 
     * @param status Processing status
     */
    public void changeStatus(WalletOperationStatusEnum status) {
        this.status = status;
        this.setUpdated(new Date());
    }

    /**
     * @return Last status change date
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated Last status change date
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public UnitOfMeasure getInputUnitOfMeasure() {
        return inputUnitOfMeasure;
    }

    public void setInput_unitOfMeasure(UnitOfMeasure inputUnitOfMeasure) {
        this.inputUnitOfMeasure = inputUnitOfMeasure;
    }

    public UnitOfMeasure getRatingUnitOfMeasure() {
        return ratingUnitOfMeasure;
    }

    public void setRatingUnitOfMeasure(UnitOfMeasure ratingUnitOfMeasure) {
        this.ratingUnitOfMeasure = ratingUnitOfMeasure;

    }
}