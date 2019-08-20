package org.meveo.cassandra.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;

/**
 * Cassandra Wallet operation record
 *
 * @author Mohamed Stitane
 */

@Entity
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class WalletOperation {

    @PartitionKey
    private UUID id;
    private String operationType;
    private Long version;
    private Instant created;
    private Instant updated;
    private String code;
    private String description;
    private BigDecimal amountTax;
    private BigDecimal amountWithTax;
    private BigDecimal amountWithoutTax;
    private Instant endDate;
    private String offerCode;
    private Instant operationDate;
    @CqlName("parameter_1")
    private String parameter1;
    @CqlName("parameter_2")
    private String parameter2;
    @CqlName("parameter_3")
    private String parameter3;
    private BigDecimal quantity;
    private Instant startDate;
    private String status;
    private Instant subscriptionDate;
    private BigDecimal taxPercent;
    private String creditDebitFlag;
    private BigDecimal unitAmountTax;
    private BigDecimal unitAmountWithTax;
    private BigDecimal unitAmountWithoutTax;
    private String creator;
    private String updater;
    private Long aggregateServId;
    private Long chargeInstanceId;
    private Long counterId;
    private Long currencyId;
    private Long priceplanId;
    private Long reratedwalletoperationId;
    private Long sellerId;
    private Long walletId;
    private Long reservationId;
    private Instant invoicingDate;
    private BigDecimal inputQuantity;
    private String inputUnitDescription;
    private String ratingUnitDescription;
    private Long edrId;
    private String orderNumber;
    private String parameterExtra;
    private BigDecimal rawAmountWithoutTax;
    private BigDecimal rawAmountWithTax;
    private Long invoiceSubCategoryId;
    private Long subscriptionId;
    private Long taxId;
    private Long ratedTransactionId;
    private Long serviceInstanceId;
    private Long offerId;

    public WalletOperation() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
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

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public Instant getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Instant operationDate) {
        this.operationDate = operationDate;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Instant subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getCreditDebitFlag() {
        return creditDebitFlag;
    }

    public void setCreditDebitFlag(String creditDebitFlag) {
        this.creditDebitFlag = creditDebitFlag;
    }

    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Long getAggregateServId() {
        return aggregateServId;
    }

    public void setAggregateServId(Long aggregateServId) {
        this.aggregateServId = aggregateServId;
    }

    public Long getChargeInstanceId() {
        return chargeInstanceId;
    }

    public void setChargeInstanceId(Long chargeInstanceId) {
        this.chargeInstanceId = chargeInstanceId;
    }

    public Long getCounterId() {
        return counterId;
    }

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getPriceplanId() {
        return priceplanId;
    }

    public void setPriceplanId(Long priceplanId) {
        this.priceplanId = priceplanId;
    }

    public Long getReratedwalletoperationId() {
        return reratedwalletoperationId;
    }

    public void setReratedwalletoperationId(Long reratedwalletoperationId) {
        this.reratedwalletoperationId = reratedwalletoperationId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Instant getInvoicingDate() {
        return invoicingDate;
    }

    public void setInvoicingDate(Instant invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    public BigDecimal getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(BigDecimal inputQuantity) {
        this.inputQuantity = inputQuantity;
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

    public Long getEdrId() {
        return edrId;
    }

    public void setEdrId(Long edrId) {
        this.edrId = edrId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
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

    public Long getInvoiceSubCategoryId() {
        return invoiceSubCategoryId;
    }

    public void setInvoiceSubCategoryId(Long invoiceSubCategoryId) {
        this.invoiceSubCategoryId = invoiceSubCategoryId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Long getRatedTransactionId() {
        return ratedTransactionId;
    }

    public void setRatedTransactionId(Long ratedTransactionId) {
        this.ratedTransactionId = ratedTransactionId;
    }

    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
}
