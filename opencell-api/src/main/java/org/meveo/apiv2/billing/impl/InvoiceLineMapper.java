package org.meveo.apiv2.billing.impl;

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
        return null;
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
