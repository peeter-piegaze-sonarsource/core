package org.meveo.service.billing.impl;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.service.base.BusinessService;
import org.meveo.service.filter.FilterService;

@Stateless
public class InvoiceLinesService extends BusinessService<InvoiceLine> {

    @Inject
    private FilterService filterService;

    public List<InvoiceLine> findByCommercialOrder(CommercialOrder commercialOrder) {
        return getEntityManager().createNamedQuery("InvoiceLine.findByCommercialOrder", InvoiceLine.class)
                .setParameter("commercialOrder", commercialOrder)
                .getResultList();
    }

    public List<InvoiceLine> listInvoiceLinesToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate,
                                                       Date lastTransactionDate, Filter filter, int pageSize) throws BusinessException {
        if (filter != null) {
            return (List<InvoiceLine>) filterService.filteredListAsObjects(filter, null);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceBySubscription", InvoiceLine.class)
                    .setParameter("subscriptionId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByBillingAccount", InvoiceLine.class)
                    .setParameter("billingAccountId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByOrderNumber", InvoiceLine.class)
                    .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
        return emptyList();
    }

    public List<InvoiceLine> loadInvoiceLinesByBRs(List<BillingRun> billingRuns) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByBRs", InvoiceLine.class)
                    .setParameter("BillingRus", billingRuns)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided billing runs");
            return emptyList();
        }
    }

    public List<InvoiceLine> loadInvoiceLinesByBRId(long BillingRunId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByBRID", InvoiceLine.class)
                    .setParameter("billingRunId", BillingRunId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided billing runs");
            return emptyList();
        }
    }
    
    public List<InvoiceLine> listInvoiceLinesByInvoice(long invoiceId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByInvoiceID", InvoiceLine.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided Invoice : "+invoiceId);
            return emptyList();
        }
    }

    public void createInvoiceLine(CommercialOrder commercialOrder, AccountingArticle accountingArticle, OrderProduct orderProduct, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setCode("COMMERCIAL-GEN");
        invoiceLine.setCode(findDuplicateCode(invoiceLine));
        invoiceLine.setAccountingArticle(accountingArticle);
        invoiceLine.setLabel(accountingArticle.getDescription());
        invoiceLine.setProduct(orderProduct.getProductVersion().getProduct());
        invoiceLine.setProductVersion(orderProduct.getProductVersion());
        invoiceLine.setCommercialOrder(commercialOrder);
        invoiceLine.setOrderLot(orderProduct.getOrderServiceCommercial());
        invoiceLine.setQuantity(BigDecimal.valueOf(1));
        invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
        invoiceLine.setAmountTax(taxAmountToBeInvoiced);
        invoiceLine.setTaxRate(totalTaxRate);
        invoiceLine.setOrderNumber(commercialOrder.getOrderNumber());
        invoiceLine.setBillingAccount(commercialOrder.getBillingAccount());
        invoiceLine.setValueDate(new Date());
        create(invoiceLine);
    }

	/**
	 * @param invoice 
	 * @param invoiceLineRessource
	 * @return 
	 */
	public void create(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource) {
		InvoiceLine invoiceLine = invoiceLineRessourceToEntity(invoiceLineRessource, null);
		invoiceLine.setCode(invoice.getCode());
		invoiceLine.setInvoice(invoice);
		create(invoiceLine);
	}
	
	protected InvoiceLine invoiceLineRessourceToEntity(org.meveo.apiv2.billing.InvoiceLine resource, InvoiceLine invoiceLine) {
		if(invoiceLine==null) {
			invoiceLine = new InvoiceLine();
		}
		Optional.ofNullable(resource.getPrestation()).ifPresent(invoiceLine::setPrestation);
		Optional.ofNullable(resource.getQuantity()).ifPresent(invoiceLine::setQuantity);
		Optional.ofNullable(resource.getUnitPrice()).ifPresent(invoiceLine::setUnitPrice);
		Optional.ofNullable(resource.getDiscountRate()).ifPresent(invoiceLine::setDiscountRate);
		Optional.ofNullable(resource.getAmountWithoutTax()).ifPresent(invoiceLine::setAmountWithoutTax);
		Optional.ofNullable(resource.getTaxRate()).ifPresent(invoiceLine::setTaxRate);
		Optional.ofNullable(resource.getAmountWithTax()).ifPresent(invoiceLine::setAmountWithTax);
		Optional.ofNullable(resource.getAmountTax()).ifPresent(invoiceLine::setAmountTax);
		Optional.ofNullable(resource.getOrderRef()).ifPresent(invoiceLine::setOrderRef);
		Optional.ofNullable(resource.getAccessPoint()).ifPresent(invoiceLine::setAccessPoint);
		Optional.ofNullable(resource.getValueDate()).ifPresent(invoiceLine::setValueDate);
		Optional.ofNullable(resource.getOrderNumber()).ifPresent(invoiceLine::setOrderNumber);
		Optional.ofNullable(resource.getDiscountAmount()).ifPresent(invoiceLine::setDiscountAmount);
		Optional.ofNullable(resource.getLabel()).ifPresent(invoiceLine::setLabel);
		Optional.ofNullable(resource.getRawAmount()).ifPresent(invoiceLine::setRawAmount);
		
		if(resource.getServiceInstanceCode()!=null) {
			invoiceLine.setServiceInstance((ServiceInstance)tryToFindByEntityClassAndCode(ServiceInstance.class, resource.getServiceInstanceCode()));
		}
		if(resource.getSubscriptionCode()!=null) {
			invoiceLine.setSubscription((Subscription)tryToFindByEntityClassAndCode(Subscription.class, resource.getSubscriptionCode()));
		}
		if(resource.getProductCode()!=null) {
			invoiceLine.setProduct((Product)tryToFindByEntityClassAndCode(Product.class, resource.getProductCode()));
		}
		if(resource.getAccountingArticleCode()!=null) {
			invoiceLine.setAccountingArticle((AccountingArticle)tryToFindByEntityClassAndCode(AccountingArticle.class, resource.getAccountingArticleCode()));
		}
		if(resource.getServiceTemplateCode()!=null) {
			invoiceLine.setServiceTemplate((ServiceTemplate)tryToFindByEntityClassAndCode(ServiceTemplate.class, resource.getServiceTemplateCode()));
		}
		if(resource.getDiscountPlanCode()!=null) {
			invoiceLine.setDiscountPlan((DiscountPlan)tryToFindByEntityClassAndCode(DiscountPlan.class, resource.getDiscountPlanCode()));
		}
		if(resource.getTaxCode()!=null) {
			invoiceLine.setTax((Tax)tryToFindByEntityClassAndCode(Tax.class, resource.getTaxCode()));
		}
		if(resource.getOrderLotCode()!=null) {
			invoiceLine.setOrderLot((OrderLot)tryToFindByEntityClassAndCode(OrderLot.class, resource.getOrderLotCode()));
		}
		if(resource.getBillingAccountCode()!=null) {
			invoiceLine.setBillingAccount((BillingAccount)tryToFindByEntityClassAndCode(BillingAccount.class, resource.getBillingAccountCode()));
		}
		if(resource.getOfferTemplateCode()!=null) {
			invoiceLine.setOfferTemplate((OfferTemplate)tryToFindByEntityClassAndCode(OfferTemplate.class, resource.getOfferTemplateCode()));
		}
		
		if(resource.isTaxRecalculated()!=null){
			invoiceLine.setTaxRecalculated( resource.isTaxRecalculated());
		}
		/*
		invoiceLine.setProductVersion((ProductVersion)tryToFindByEntityClassAndCode(ProductVersion.class, resource.getProductVersionCode()));
		invoiceLine.setOfferServiceTemplate((OfferServiceTemplate)tryToFindByEntityClassAndCode(OfferServiceTemplate.class, resource.getOfferServiceTemplateCode()));
		invoiceLine.setCommercialOrder((CommercialOrder)tryToFindByEntityClassAndCode(CommercialOrder.class, resource.getCommercialOrderCode()));
		invoiceLine.setBillingRun((BillingRun)tryToFindByEntityClassAndCode(BillingRun.class, resource.getBillingRunCode()));
		 */
		
		return invoiceLine;
	}
	
    public BusinessEntity tryToFindByEntityClassAndCode(Class<? extends BusinessEntity> entity, String code) {
    	if(code==null) {
    		return null;
    	}
        QueryBuilder qb = new QueryBuilder(entity, "entity", null);
        qb.addCriterion("entity.code", "=", code, true);
        try {
			return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("No entity of type "+entity.getSimpleName()+"with code '"+code+"' found");
        } catch (NonUniqueResultException e) {
        	throw new ForbiddenException("More than one entity of type "+entity.getSimpleName()+" with code '"+code+"' found");
        }
    }

	/**
	 * @param invoice
	 * @param invoiceLine
	 */
	public void update(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource, Long invoiceLineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, invoiceLineId);
		invoiceLine = invoiceLineRessourceToEntity(invoiceLineRessource, invoiceLine);
		update(invoiceLine);
	}

	private InvoiceLine findInvoiceLine(Invoice invoice, Long invoiceLineId) {
		InvoiceLine invoiceLine = findById(invoiceLineId);
		if(!invoice.equals(invoiceLine.getInvoice())) {
			throw new BusinessException("invoice line with ID "+invoiceLineId+" is not related to invoice with id:"+invoice.getId());
		}
		return invoiceLine;
	}

	/**
	 * @param invoice
	 * @param lineId
	 */
	public void remove(Invoice invoice, Long lineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, lineId);
		invoiceLine.setStatus(InvoiceLineStatusEnum.CANCELED);
		invoiceLine.setInvoice(null);
	}
}
