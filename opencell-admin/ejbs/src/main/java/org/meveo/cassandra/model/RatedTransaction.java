package org.meveo.cassandra.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;

import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;

/**
 * Cassandra Rated transaction record
 * 
 * @author Mohamed Stitane
 */

@Entity
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class RatedTransaction {

    @PartitionKey
    private UUID id;

    private long version = 1;

    private Long walletId;

    private Long billingAccountId;

    private Long chargeInstanceId;

    private Long sellerId;

    private Long billingRunId;

    private Long subscriptionId;

    private Instant usageDate;

    private Long invoiceSubCategoryId;

    private String code;

    private String description;

    private String unityDescription;

    private String ratingUnitDescription;

    private BigDecimal unitAmountWithoutTax;

    private BigDecimal unitAmountWithTax;

    private BigDecimal unitAmountTax;

    private BigDecimal quantity;

    private BigDecimal amountWithoutTax;

    private BigDecimal amountWithTax;

    private BigDecimal amountTax;

    private Long invoiceId;

    private Long aggregateIdF;

    private Long aggregateIdR;

    private Long aggregateIdT;

    private String status;

    private long doNotTriggerInvoicing;

    @CqlName("parameter_1")
    private String parameter1;
    @CqlName("parameter_2")
    private String parameter2;
    @CqlName("parameter_3")
    private String parameter3;

    private Instant startDate;

    private Instant endDate;

    private String parameterExtra;

    private String orderNumber;

    private Long priceplanId;

    private Long offerId;

    private Long edrId;

    private Long adjustedRatedTx;

    private Long taxId;

    private BigDecimal taxPercent;

    private Long userAccountId;

    private Long serviceInstanceId;

    public RatedTransaction() {
    }

    public RatedTransaction(WalletOperation walletOperation) {

        this.id = UUID.randomUUID();
        this.code = walletOperation.getCode();
        this.description = walletOperation.getDescription();
        this.chargeInstanceId = walletOperation.getChargeInstance() != null ? walletOperation.getChargeInstance().getId() : null;
        this.usageDate = walletOperation.getOperationDate() != null ? walletOperation.getOperationDate().toInstant() : null;
        this.unitAmountWithoutTax = walletOperation.getUnitAmountWithoutTax();
        this.unitAmountWithTax = walletOperation.getUnitAmountWithTax();
        this.unitAmountTax = walletOperation.getUnitAmountTax();
        this.quantity = walletOperation.getQuantity();
        this.amountWithoutTax = walletOperation.getAmountWithoutTax();
        this.amountWithTax = walletOperation.getAmountWithTax();
        this.amountTax = walletOperation.getAmountTax();
        this.status = RatedTransactionStatusEnum.OPEN.name();
        final WalletInstance walletInstance = walletOperation.getWallet();
        if (walletInstance != null) {
            this.walletId = walletInstance.getId();
            final UserAccount userAccount = walletInstance.getUserAccount();
            if (userAccount != null) {
                this.userAccountId = userAccount.getId();
                if (userAccount.getBillingAccount() != null) {
                    this.billingAccountId = userAccount.getBillingAccount().getId();
                }
            }
        }
        this.sellerId = Optional.ofNullable(walletOperation.getSeller()).map(Seller::getId).orElse(null);
        this.invoiceSubCategoryId = Optional.ofNullable(walletOperation.getInvoiceSubCategory())
                .map(InvoiceSubCategory::getId).orElse(null);
        this.parameter1 = walletOperation.getParameter1();
        this.parameter2 = walletOperation.getParameter2();
        this.parameter3 = walletOperation.getParameter3();
        this.parameterExtra = walletOperation.getParameterExtra();
        this.orderNumber = walletOperation.getOrderNumber();
        if (walletOperation.getSubscription() != null) {
            this.subscriptionId = walletOperation.getSubscription().getId();
        }
        if (walletOperation.getPriceplan() != null) {
            this.priceplanId = walletOperation.getPriceplan().getId();
        }
        if (walletOperation.getOfferTemplate() != null) {
            this.offerId = walletOperation.getOfferTemplate().getId();
        }
        if (walletOperation.getEdr() != null) {
            this.edrId = walletOperation.getEdr().getId();
        }
        if (walletOperation.getStartDate() != null) {
            this.startDate = walletOperation.getStartDate().toInstant();
        }
        if (walletOperation.getEndDate() != null) {
            this.endDate = walletOperation.getEndDate().toInstant();
        }
        if (walletOperation.getTax() != null) {
            this.taxId = walletOperation.getTax().getId();
        }
        this.taxPercent = walletOperation.getTaxPercent();
        if (walletOperation.getServiceInstance() != null) {
            this.serviceInstanceId = walletOperation.getServiceInstance().getId();
        }

        this.unityDescription = walletOperation.getInputUnitDescription();
        if (this.unityDescription == null) {
            this.unityDescription = walletOperation.getChargeInstance().getChargeTemplate().getInputUnitDescription();
        }
        this.ratingUnitDescription = walletOperation.getRatingUnitDescription();
        if (ratingUnitDescription == null) {
            this.ratingUnitDescription = walletOperation.getChargeInstance().getChargeTemplate().getRatingUnitDescription();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getBillingAccountId() {
        return billingAccountId;
    }

    public void setBillingAccountId(Long billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public Long getChargeInstanceId() {
        return chargeInstanceId;
    }

    public void setChargeInstanceId(Long chargeInstanceId) {
        this.chargeInstanceId = chargeInstanceId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getBillingRunId() {
        return billingRunId;
    }

    public void setBillingRunId(Long billingRunId) {
        this.billingRunId = billingRunId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Instant getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Instant usageDate) {
        this.usageDate = usageDate;
    }

    public Long getInvoiceSubCategoryId() {
        return invoiceSubCategoryId;
    }

    public void setInvoiceSubCategoryId(Long invoiceSubCategoryId) {
        this.invoiceSubCategoryId = invoiceSubCategoryId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnityDescription() {
        return unityDescription;
    }

    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getAggregateIdF() {
        return aggregateIdF;
    }

    public void setAggregateIdF(Long aggregateIdF) {
        this.aggregateIdF = aggregateIdF;
    }

    public Long getAggregateIdR() {
        return aggregateIdR;
    }

    public void setAggregateIdR(Long aggregateIdR) {
        this.aggregateIdR = aggregateIdR;
    }

    public Long getAggregateIdT() {
        return aggregateIdT;
    }

    public void setAggregateIdT(Long aggregateIdT) {
        this.aggregateIdT = aggregateIdT;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    public void setDoNotTriggerInvoicing(long doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
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

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getPriceplanId() {
        return priceplanId;
    }

    public void setPriceplanId(Long priceplanId) {
        this.priceplanId = priceplanId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getEdrId() {
        return edrId;
    }

    public void setEdrId(Long edrId) {
        this.edrId = edrId;
    }

    public Long getAdjustedRatedTx() {
        return adjustedRatedTx;
    }

    public void setAdjustedRatedTx(Long adjustedRatedTx) {
        this.adjustedRatedTx = adjustedRatedTx;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String toString() {
        return "RatedTransaction{" + "id=" + id + ", version=" + version + ", walletId=" + walletId + ", billingAccountId=" + billingAccountId + ", chargeInstanceId="
                + chargeInstanceId + ", sellerId=" + sellerId + ", billingRunId=" + billingRunId + ", subscriptionId=" + subscriptionId + ", usageDate=" + usageDate
                + ", invoiceSubCategoryId=" + invoiceSubCategoryId + ", code='" + code + '\'' + ", description='" + description + '\'' + ", unityDescription='" + unityDescription
                + '\'' + ", ratingUnitDescription='" + ratingUnitDescription + '\'' + ", unitAmountWithoutTax=" + unitAmountWithoutTax + ", unitAmountWithTax=" + unitAmountWithTax
                + ", unitAmountTax=" + unitAmountTax + ", quantity=" + quantity + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", amountTax="
                + amountTax + ", invoiceId=" + invoiceId + ", aggregateIdF=" + aggregateIdF + ", aggregateIdR=" + aggregateIdR + ", aggregateIdT=" + aggregateIdT + ", status='"
                + status + '\'' + ", doNotTriggerInvoicing=" + doNotTriggerInvoicing + ", parameter1='" + parameter1 + '\'' + ", parameter2='" + parameter2 + '\''
                + ", parameter3='" + parameter3 + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", parameterExtra='" + parameterExtra + '\'' + ", orderNumber='"
                + orderNumber + '\'' + ", priceplanId=" + priceplanId + ", offerId=" + offerId + ", edrId=" + edrId + ", adjustedRatedTx=" + adjustedRatedTx + ", taxId=" + taxId
                + ", taxPercent=" + taxPercent + ", userAccountId=" + userAccountId + ", serviceInstanceId=" + serviceInstanceId + '}';
    }
}