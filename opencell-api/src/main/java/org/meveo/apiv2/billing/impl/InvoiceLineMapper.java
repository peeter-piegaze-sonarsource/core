package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;

public class InvoiceLineMapper extends ResourceMapper<org.meveo.apiv2.billing.InvoiceLine, InvoiceLine> {


    @Override
    protected org.meveo.apiv2.billing.InvoiceLine toResource(InvoiceLine entity) {

        return ImmutableInvoiceLine.builder()
                .invoice(entity.getInvoice() != null ? ImmutableResource.builder().id(entity.getInvoice().getId()).build() : null)
                .billingRun(entity.getBillingRun() != null ? ImmutableResource.builder().id(entity.getBillingRun().getId()).build() : null)
                .billingAccount(ImmutableResource.builder().id(entity.getBillingAccount().getId()).build())
                .accountingArticle(ImmutableResource.builder().id(entity.getAccountingArticle().getId()).build())
                .label(entity.getLabel())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .discountRate(entity.getDiscountRate())
                .discountAmountWithoutTax(entity.getDiscountAmountWithoutTax())
                .amountWithoutTax(entity.getAmountWithoutTax())
                .taxRate(entity.getTaxRate())
                .amountTax(entity.getAmountTax())
                .amountWithTax(entity.getAmountWithTax())
                .prestation(entity.getPrestation())
                .offerTemplate(entity.getOffer() != null ? ImmutableResource.builder().id(entity.getOffer().getId()).build() : null)
                .productInstance(entity.getProduct() != null ? ImmutableResource.builder().id(entity.getProduct().getId()).build() : null)
                .serviceInstance(entity.getService() != null ? ImmutableResource.builder().id(entity.getService().getId()).build() : null)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .discountPlan(entity.getDiscountPlan() != null ? ImmutableResource.builder().id(entity.getDiscountPlan().getId()).build() : null)
                .tax(entity.getTax() != null ? ImmutableResource.builder().id(entity.getTax().getId()).build() : null)
                .orderNumber(entity.getOrderNumber())
                .order(entity.getOrder() != null ? ImmutableResource.builder().id(entity.getId()).build() : null)
                .build();
    }

    @Override
    protected InvoiceLine toEntity(org.meveo.apiv2.billing.InvoiceLine resource) {
        InvoiceLine invoiceLine = new InvoiceLine();
        if (resource.getInvoice() != null) {
            Invoice invoice = new Invoice();
            invoice.setId(resource.getInvoice().getId());
            invoiceLine.setInvoice(invoice);
        }
        if (resource.getBillingRun() != null) {
            BillingRun billingRun = new BillingRun();
            billingRun.setId(resource.getBillingRun().getId());
            invoiceLine.setBillingRun(billingRun);
        }

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setId(resource.getBillingAccount().getId());
        invoiceLine.setBillingAccount(billingAccount);

        AccountingArticle accountingArticle = new AccountingArticle();
        accountingArticle.setId(resource.getAccountingArticle().getId());
        invoiceLine.setAccountingArticle(accountingArticle);

        invoiceLine.setLabel(resource.getLabel());
        invoiceLine.setQuantity(resource.getQuantity());
        invoiceLine.setUnitPrice(resource.getUnitPrice());
        invoiceLine.setDiscountRate(resource.getDiscountRate());
        invoiceLine.setDiscountAmountWithoutTax(resource.getDiscountAmountWithoutTax());
        invoiceLine.setAmountWithTax(resource.getAmountWithTax());
        invoiceLine.setPrestation(resource.getPrestation());

        if(resource.getOfferTemplate() != null){
            OfferTemplate offerTemplate = new OfferTemplate();
            offerTemplate.setId(resource.getOfferTemplate().getId());
            invoiceLine.setOffer(offerTemplate);
        }

        if(resource.getServiceInstance() != null){
            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setId(resource.getServiceInstance().getId());
            invoiceLine.setService(serviceInstance);
        }
        if(resource.getProductInstance() != null){
            ProductInstance productInstance = new ProductInstance();
            productInstance.setId(resource.getProductInstance().getId());
            invoiceLine.setProduct(productInstance);
        }

        invoiceLine.setStartDate(resource.getStartDate());
        invoiceLine.setEndDate(resource.getEndDate());

        if(resource.getDiscountPlan() != null){
            DiscountPlan discountPlan = new DiscountPlan();
            discountPlan.setId(resource.getDiscountPlan().getId());
            invoiceLine.setDiscountPlan(discountPlan);
        }

        if(resource.getTax() != null){
            Tax tax = new Tax();
            tax.setId(resource.getTax().getId());
            invoiceLine.setTax(tax);
        }

        invoiceLine.setOrderNumber(resource.getOrderNumber());

        if(resource.getOrder() != null){
            Order order = new Order();
            order.setId(resource.getOrder().getId());
            invoiceLine.setOrder(order);
        }

        if(resource.getAccess() != null){
            Access access = new Access();
            access.setId(resource.getAccess().getId());
            invoiceLine.setAccessPoint(access);
        }

        return invoiceLine;
    }
}
