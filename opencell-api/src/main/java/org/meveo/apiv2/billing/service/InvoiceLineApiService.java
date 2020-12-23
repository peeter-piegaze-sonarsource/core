package org.meveo.apiv2.billing.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.article.AccountingArticleService1;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.order.OrderService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class InvoiceLineApiService implements ApiService<InvoiceLine> {

    @Inject
    private InvoiceLineService invoiceLineService;
    @Inject
    private InvoiceService invoiceService;
    @Inject
    private BillingRunService billingRunService;
    @Inject
    private BillingAccountService billingAccountService;
    @Inject
    private AccountingArticleService1 accountingArticleService1;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private ProductInstanceService productInstanceService;
    @Inject
    private ServiceInstanceService serviceInstanceService;
    @Inject
    private TaxService taxService;
    @Inject
    private OrderService orderService;
    @Inject
    private DiscountPlanService discountPlanService;

    @Override
    public List<InvoiceLine> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<InvoiceLine> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public InvoiceLine create(InvoiceLine baseEntity) {
        if(baseEntity.getInvoice() != null)
            baseEntity.setInvoice(loadEntityById(invoiceService, baseEntity.getInvoice().getId(), "invoice"));
        if(baseEntity.getBillingRun() != null)
            baseEntity.setBillingRun(loadEntityById(billingRunService, baseEntity.getBillingRun().getId(), "billing run"));
        baseEntity.setBillingAccount(loadEntityById(billingAccountService, baseEntity.getBillingAccount().getId(), "billing account"));
        baseEntity.setAccountingArticle(loadEntityById(accountingArticleService1, baseEntity.getAccountingArticle().getId(), "accounting article"));
        if(baseEntity.getOffer() != null)
            baseEntity.setOffer(loadEntityById(offerTemplateService, baseEntity.getOffer().getId(), "offer template"));
        if(baseEntity.getProduct() != null)
            baseEntity.setProduct(loadEntityById(productInstanceService, baseEntity.getProduct().getId(), "product instance"));
        if(baseEntity.getService() != null)
            baseEntity.setService(loadEntityById(serviceInstanceService, baseEntity.getService().getId(), "service instance"));
        if(baseEntity.getDiscountPlan() != null)
            baseEntity.setDiscountPlan(loadEntityById(discountPlanService, baseEntity.getDiscountPlan().getId(), "discount plan"));
        if(baseEntity.getTax() != null)
            baseEntity.setTax(loadEntityById(taxService, baseEntity.getTax().getId(), "tax"));
        if(baseEntity.getOrder() != null)
            baseEntity.setOrder(loadEntityById(orderService, baseEntity.getOrder().getId(), "order service"));
        invoiceLineService.create(baseEntity);
        return baseEntity;
    }

    @Override
    public Optional<InvoiceLine> update(Long id, InvoiceLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> patch(Long id, InvoiceLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> delete(Long id) {
        return Optional.empty();
    }
}
