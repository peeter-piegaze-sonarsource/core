package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import java.math.BigDecimal;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableInvoiceLine.class)
public interface InvoiceLine extends Resource{

    String getCode();

    Resource getInvoice();

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

    String getPrestation();

    Resource getOfferTemplate();

    Resource getProductInstance();

    Resource getServiceInstance();

    Date getStartDate();

    Date getEndDate();

    Resource getDiscountPlan();

    Resource getTax();

    String getOrderNumber();

    Resource getOrder();

    Resource getAccess();


}
