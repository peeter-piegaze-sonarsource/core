package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableInvoiceLine.class)
public interface InvoiceLine extends Resource{

    String getCode();

    @Nullable
    Resource getInvoice();

    @Nullable
    Resource getBillingRun();

    Resource getBillingAccount();

    Resource getAccountingArticle();

    String getLabel();

    Long getQuantity();

    BigDecimal getUnitPrice();

    BigDecimal getDiscountRate();

    BigDecimal getDiscountAmountWithoutTax();

    BigDecimal getAmountWithoutTax();

    BigDecimal getTaxRate();

    BigDecimal getAmountTax();

    BigDecimal getAmountWithTax();

    @Nullable
    String getPrestation();

    @Nullable
    Resource getOfferTemplate();

    @Nullable
    Resource getProductInstance();

    @Nullable
    Resource getServiceInstance();

    @Nullable
    Date getStartDate();

    @Nullable
    Date getEndDate();

    @Nullable
    Resource getDiscountPlan();

    @Nullable
    Resource getTax();

    @Nullable
    String getOrderNumber();

    @Nullable
    Resource getOrder();

    @Nullable
    Resource getAccess();


}
