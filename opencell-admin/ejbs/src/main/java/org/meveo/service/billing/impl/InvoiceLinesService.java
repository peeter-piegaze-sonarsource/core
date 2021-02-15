package org.meveo.service.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.*;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.tax.TaxMappingService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.meveo.model.billing.InvoiceLineStatusEnum.OPEN;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.*;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

@Stateless
public class InvoiceLinesService extends BusinessService<InvoiceLine> {

    @Inject
    private FilterService filterService;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private AccountingArticleService accountingArticleService;

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
    
    public List<InvoiceLine> listInvoiceLinesByInvoice(long invoiceId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByInvoiceID", InvoiceLine.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided Invoice : " + invoiceId);
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

    public void calculateAmountsAndCreateMinAmountLines(IBillableEntity billableEntity, Date lastTransactionDate,
                                                        boolean calculateAndUpdateTotalAmounts, MinAmountForAccounts minAmountForAccounts) throws BusinessException {
        Amounts totalInvoiceableAmounts;
        List<InvoiceLine> minAmountLines = new ArrayList<>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();
        Date minRatingDate = addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order) {
            if (calculateAndUpdateTotalAmounts) {
                totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            }
        } else {
            BillingAccount billingAccount =
                    (billableEntity instanceof Subscription) ? ((Subscription) billableEntity).getUserAccount().getBillingAccount() : (BillingAccount) billableEntity;

            Class[] accountClasses = new Class[] { ServiceInstance.class, Subscription.class,
                    UserAccount.class, BillingAccount.class, CustomerAccount.class, Customer.class };
            for (Class accountClass : accountClasses) {
                if (minAmountForAccounts.isMinAmountForAccountsActivated(accountClass, billableEntity)) {
                    MinAmountsResult minAmountsResults = createMinILForAccount(billableEntity, billingAccount,
                            lastTransactionDate, minRatingDate, extraMinAmounts, accountClass);
                    extraMinAmounts = minAmountsResults.getExtraMinAmounts();
                    minAmountLines.addAll(minAmountsResults.getMinAmountInvoiceLines());
                }
            }
            totalInvoiceableAmounts = computeTotalInvoiceableAmount(billableEntity, new Date(0), lastTransactionDate);
            final Amounts totalAmounts = new Amounts();
            extraMinAmounts.forEach(extraMinAmount -> {
                extraMinAmount.getCreatedAmount().values().forEach(amounts -> {
                    totalAmounts.addAmounts(amounts);
                });
            });
            totalInvoiceableAmounts.addAmounts(totalAmounts);
        }
    }

    private Amounts computeTotalOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {
        String queryString = "InvoiceLine.sumTotalInvoiceableByOrderNumber";
        Query query = getEntityManager().createNamedQuery(queryString)
                .setParameter("orderNumber", order.getOrderNumber())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
        return (Amounts) query.getSingleResult();
    }

    private MinAmountsResult createMinILForAccount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                   Date lastTransactionDate, Date minRatingDate, List<ExtraMinAmount> extraMinAmounts,
                                                   Class accountClass) throws BusinessException {
        MinAmountsResult minAmountsResult = new MinAmountsResult();
        Map<Long, MinAmountData> accountToMinAmount =
                getInvoiceableAmountDataPerAccount(billableEntity, billingAccount, lastTransactionDate, extraMinAmounts, accountClass);
        accountToMinAmount = prepareAccountsWithMinAmount(billableEntity, billingAccount, extraMinAmounts, accountClass, accountToMinAmount);

        for (Map.Entry<Long, MinAmountData> accountAmounts : accountToMinAmount.entrySet()) {
            Map<String, Amounts> minRTAmountMap = new HashMap<>();
            if (accountAmounts.getValue() == null || accountAmounts.getValue().getMinAmount() == null) {
                continue;
            }
            BigDecimal minAmount = accountAmounts.getValue().getMinAmount();
            String minAmountLabel = accountAmounts.getValue().getMinAmountLabel();
            BigDecimal totalInvoiceableAmount =
                    appProvider.isEntreprise() ? accountAmounts.getValue().getAmounts().getAmountWithoutTax() : accountAmounts.getValue().getAmounts().getAmountWithTax();
            BusinessEntity entity = accountAmounts.getValue().getEntity();
            Seller seller = accountAmounts.getValue().getSeller();
            if (seller == null) {
                throw new BusinessException("Default Seller is mandatory for invoice minimum (Customer.seller)");
            }
            String mapKeyPrefix = seller.getId().toString() + "_";
            BigDecimal diff = minAmount.subtract(totalInvoiceableAmount);
            if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            AccountingArticle defaultMinAccountingArticle = getDefaultAccountingArticle();
            if (defaultMinAccountingArticle == null) {
                log.error("No default AccountingArticle defined");
                continue;
            }
            InvoiceSubCategory invoiceSubCategory = defaultMinAccountingArticle.getInvoiceSubCategory();
            String mapKey = mapKeyPrefix + invoiceSubCategory.getId();
            TaxMappingService.TaxInfo taxInfo = taxMappingService.determineTax(defaultMinAccountingArticle.getTaxClass(), seller, billingAccount,
                    null, minRatingDate, true, false);
            String code = getMinAmountInvoiceLineCode(entity, accountClass);
            InvoiceLine invoiceLine = createInvoiceLine(code, minAmountLabel, billableEntity, billingAccount, minRatingDate,
                    entity, seller, defaultMinAccountingArticle, taxInfo, diff);
            minAmountsResult.addMinAmountIL(invoiceLine);
            minRTAmountMap.put(mapKey, new Amounts(invoiceLine.getAmountWithoutTax(), invoiceLine.getAmountWithTax(), invoiceLine.getAmountTax()));
            extraMinAmounts.add(new ExtraMinAmount(entity, minRTAmountMap));
        }
        minAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return minAmountsResult;
    }

    private Map<Long, MinAmountData> getInvoiceableAmountDataPerAccount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                                        Date lastTransactionDate, List<ExtraMinAmount> extraMinAmounts, Class accountClass) {
        Map<Long, MinAmountData> accountToMinAmount = new HashMap<>();
        List<Object[]> amountsList = computeInvoiceableAmountForAccount(billableEntity, new Date(0), lastTransactionDate, accountClass);
        for (Object[] amounts : amountsList) {
            BigDecimal amountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal amountWithTax = (BigDecimal) amounts[1];
            BusinessEntity entity = (BusinessEntity) getEntityManager().find(accountClass, amounts[2]);
            Seller seller = getSeller(billingAccount, entity);
            MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());
            if (minAmountDataInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);
                if (minAmount == null) {
                    continue;
                }
                MinAmountData minAmountData = new MinAmountData(minAmount, minAmountLabel, new Amounts(), null, entity, seller);
                accountToMinAmount.put(entity.getId(), minAmountData);

                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }
            }

            minAmountDataInfo = accountToMinAmount.get(entity.getId());
            minAmountDataInfo.getAmounts().addAmounts(amountWithoutTax, amountWithTax, null);

        }
        return accountToMinAmount;
    }

    private List<Object[]> computeInvoiceableAmountForAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Class accountClass) {
        if (accountClass.equals(ServiceInstance.class)) {
            return computeInvoiceableAmountForServicesWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Subscription.class)) {
            return computeInvoiceableAmountForSubscriptionsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(UserAccount.class)) {
            return computeInvoiceableAmountForUserAccountsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return computeInvoiceableAmountForBillingAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return computeInvoiceableAmountForCustomerAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Customer.class)) {
            return computeInvoiceableAmountForCustomerWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        return null;
    }

    private List<Object[]> computeInvoiceableAmountForServicesWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableByServiceWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableByServiceWithMinAmountByBillingAccount")
                    .setParameter("billingAccount", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }
        return null;
    }

    private List<Object[]> computeInvoiceableAmountForSubscriptionsWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountByBillingAccount")
                    .setParameter("billingAccount", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }
        return null;
    }

    private List<Object[]> computeInvoiceableAmountForUserAccountsWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForUAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }
        return getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableWithMinAmountByUA")
                .setParameter("billingAccount", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate).getResultList();
    }

    private List<Object[]> computeInvoiceableAmountForBillingAccountWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableForBAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }
        return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableWithMinAmountByBillingAccount")
                .setParameter("billingAccount", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .getResultList();
    }

    private List<Object[]> computeInvoiceableAmountForCustomerAccountWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableForCAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }

        return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableWithMinAmountByCA")
                .setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .getResultList();
    }

    private List<Object[]> computeInvoiceableAmountForCustomerWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableForCustomerWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .getResultList();
        }
        return getEntityManager().createNamedQuery("InvoiceLine.sumInvoiceableWithMinAmountByCustomer")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .getResultList();
    }

    private Seller getSeller(BillingAccount billingAccount, BusinessEntity entity) {
        if (entity instanceof ServiceInstance) {
            return ((ServiceInstance) entity).getSubscription().getSeller();
        }
        if (entity instanceof Subscription) {
            return ((Subscription) entity).getSeller();
        }
        return billingAccount.getCustomerAccount().getCustomer().getSeller();
    }

    private String getMinimumAmountElInfo(BusinessEntity entity, String method) {
        try {
            Method getMinimumAmountElMethod = entity.getClass().getMethod(method);
            if (getMinimumAmountElMethod != null) {
                String value = (String) getMinimumAmountElMethod.invoke(entity);
                if (value == null && entity instanceof ServiceInstance) {
                    getMinimumAmountElMethod = ((ServiceInstance) entity).getServiceTemplate().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((ServiceInstance) entity).getServiceTemplate());
                }
                if (value == null && entity instanceof Subscription) {
                    getMinimumAmountElMethod = ((Subscription) entity).getOffer().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((Subscription) entity).getOffer());
                }
                return value;
            } else {
                throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
        }
    }

    private BigDecimal evaluateMinAmountExpression(String expression, BusinessEntity entity) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
    }

    private Map<Object, Object> constructElContext(String expression, BillingAccount ba, Subscription subscription,
                                                   ServiceInstance serviceInstance, UserAccount ua) {
        Map<Object, Object> contextMap = new HashMap<>();
        if (expression.startsWith("#{")) {
            if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_SUBSCRIPTION) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_SUBSCRIPTION, subscription);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_OFFER, subscription.getOffer());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
                if (ua == null) {
                    ua = subscription != null ? subscription.getUserAccount() : serviceInstance.getSubscription().getUserAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, ua);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, ba);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, ba.getCustomerAccount());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_SHORT, ba.getCustomerAccount().getCustomer());
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER, ba.getCustomerAccount().getCustomer());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
            }
        }
        return contextMap;
    }

    private String evaluateMinAmountLabelExpression(String expression, BusinessEntity entity) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }
        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    private Map<Long, MinAmountData> appendExtraAmount(List<ExtraMinAmount> extraMinAmounts, Map<Long, MinAmountData> accountToMinAmount, BusinessEntity entity) {
        MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());
        extraMinAmounts.forEach(extraMinAmount -> {
            BusinessEntity extraMinAmountEntity = extraMinAmount.getEntity();
            if (isExtraMinAmountEntityChildOfEntity(extraMinAmountEntity, entity)) {
                Map<String, Amounts> extraAmounts = extraMinAmount.getCreatedAmount();
                for (Map.Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                    minAmountDataInfo.getAmounts().addAmounts(amountInfo.getValue());
                }
            }

        });
        return accountToMinAmount;
    }

    private boolean isExtraMinAmountEntityChildOfEntity(BusinessEntity child, BusinessEntity parent) {
        if (parent instanceof Subscription && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().equals(parent);
        }
        if (parent instanceof Customer && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof CustomerAccount) {
            return ((CustomerAccount) child).getCustomer().equals(parent);
        }
        return false;
    }

    private Map<Long, MinAmountData> prepareAccountsWithMinAmount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                                  List<ExtraMinAmount> extraMinAmounts, Class accountClass, Map<Long, MinAmountData> accountToMinAmount) {
        List<BusinessEntity> accountsWithMinAmount = getAccountsWithMinAmountElNotNull(billableEntity, accountClass);
        for (BusinessEntity entity : accountsWithMinAmount) {
            MinAmountData minAmountInfo = accountToMinAmount.get(entity.getId());
            if (minAmountInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);
                Amounts accountAmounts = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                accountToMinAmount.put(entity.getId(), new MinAmountData(minAmount, minAmountLabel, accountAmounts.clone(),
                        null, entity, getSeller(billingAccount, entity)));
                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }

            } else {
                if ((minAmountInfo.getMinAmount()).compareTo(appProvider.isEntreprise() ? minAmountInfo.getAmounts().getAmountWithoutTax()
                        : minAmountInfo.getAmounts().getAmountWithTax()) <= 0) {
                    accountToMinAmount.put(entity.getId(), null);
                }
            }
        }
        return accountToMinAmount;
    }

    private List<BusinessEntity> getAccountsWithMinAmountElNotNull(IBillableEntity billableEntity, Class<? extends BusinessEntity> accountClass) {
        if (accountClass.equals(ServiceInstance.class)) {
            return getServicesWithMinAmount(billableEntity);
        }
        if (accountClass.equals(Subscription.class)) {
            return getSubscriptionsWithMinAmount(billableEntity);
        }
        if (accountClass.equals(UserAccount.class)) {
            return getUserAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return getBillingAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return getCustomerAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(Customer.class)) {
            return getCustomersWithMinAmountELNotNull(billableEntity);
        }
        return new ArrayList<>();
    }

    private List<BusinessEntity> getServicesWithMinAmount(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountByBA")
                    .setParameter("billingAccount", billableEntity)
                    .getResultList();
        }
        return emptyList();
    }

    private List<BusinessEntity> getSubscriptionsWithMinAmount(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountByBA")
                    .setParameter("billingAccount", billableEntity)
                    .getResultList();
        }
        return emptyList();
    }

    private List<BusinessEntity> getUserAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByUA")
                    .setParameter("userAccount", ((Subscription) billableEntity).getUserAccount())
                    .getResultList();
        }
        return getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByBA")
                .setParameter("billingAccount", billableEntity)
                .getResultList();
    }

    private List<BusinessEntity> getBillingAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA")
                .setParameter("billingAccount", billableEntity)
                .getResultList();
    }

    private List<BusinessEntity> getCustomerAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("CustomerAccount.getCustomerAccountsWithMinAmountELNotNullByBA")
                .setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
                .getResultList();
    }

    private List<BusinessEntity> getCustomersWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("Customer.getCustomersWithMinAmountELNotNullByBA")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer())
                .getResultList();
    }

    private String getMinAmountInvoiceLineCode(BusinessEntity entity, Class accountClass) {
        StringBuilder prefix = new StringBuilder("");
        if (accountClass.equals(ServiceInstance.class)) {
            prefix.append(IL_MIN_AMOUNT_SE.getCode());
        }
        if (accountClass.equals(Subscription.class)) {
            prefix.append(IL_MIN_AMOUNT_SU.getCode());
        }
        if (accountClass.equals(UserAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_UA.getCode());
        }
        if (accountClass.equals(BillingAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_BA.getCode());
        }
        if (accountClass.equals(CustomerAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_CA.getCode());
        }
        if (accountClass.equals(Customer.class)) {
            prefix.append(IL_MIN_AMOUNT_CUST.getCode());
        }
        return prefix.append("_")
                .append(entity.getCode())
                .toString();
    }

    private InvoiceLine createInvoiceLine(String code, String minAmountLabel, IBillableEntity billableEntity, BillingAccount billingAccount, Date minRatingDate,
                                          BusinessEntity entity, Seller seller, AccountingArticle defaultAccountingArticle,
                                          TaxMappingService.TaxInfo taxInfo, BigDecimal ilMinAmount) {
        Tax tax = taxInfo.tax;
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(ilMinAmount, ilMinAmount, tax.getPercent(), appProvider.isEntreprise(),
                appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        InvoiceLine invoiceLine = new InvoiceLine(minRatingDate, BigDecimal.ONE, amounts[0], amounts[1], amounts[2], OPEN,
                billingAccount, code, minAmountLabel, tax, tax.getPercent(), defaultAccountingArticle);
        if (entity instanceof ServiceInstance) {
            invoiceLine.setServiceInstance((ServiceInstance) entity);
        }
        if (entity instanceof Subscription) {
            invoiceLine.setSubscription((Subscription) entity);
        }
        if (billableEntity instanceof Subscription) {
            invoiceLine.setSubscription((Subscription) billableEntity);
        }
        if(invoiceLine.getSubscription() != null) {
            invoiceLine.getSubscription().setSeller(seller);
        }
        return invoiceLine;
    }

    private AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("advancePayment.accountingArticleCode", "MIN-STD");

        AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }

    private Amounts computeTotalInvoiceableAmount(IBillableEntity billableEntity, Date date, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return computeTotalInvoiceableAmountForSubscription((Subscription) billableEntity, date, lastTransactionDate);
        }
        return computeTotalInvoiceableAmountForBillingAccount((BillingAccount) billableEntity, date, lastTransactionDate);
    }

    private Amounts computeTotalInvoiceableAmountForSubscription(Subscription subscription, Date firstTransactionDate, Date lastTransactionDate) {
        return (Amounts) getEntityManager().createNamedQuery("InvoiceLine.sumTotalInvoiceableBySubscription")
                .setParameter("subscription", subscription)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .getResultList();
    }

    private Amounts computeTotalInvoiceableAmountForBillingAccount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        return (Amounts) getEntityManager().createNamedQuery("InvoiceLine.sumTotalInvoiceableByBA")
                .setParameter("billingAccount", billingAccount)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate",
                        lastTransactionDate)
                .getResultList();
    }

    public MinAmountForAccounts isMinAmountForAccountsActivated(IBillableEntity entity, ApplyMinimumModeEnum applyMinimumModeEnum) {
        return new MinAmountForAccounts(isMinILsUsed(), entity, applyMinimumModeEnum);
    }

    public boolean[] isMinILsUsed() {
        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        boolean uaMin = false;
        boolean caMin = false;
        boolean custMin = false;
        try {
            getEntityManager()
                    .createNamedQuery("BillingAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            baMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("UserAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            uaMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("Subscription.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            subMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("ServiceInstance.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            servMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("CustomerAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            caMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("Customer.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            custMin = true;
        } catch (NoResultException e) {
        }
        return new boolean[] { servMin, subMin, uaMin, baMin, caMin, custMin };
    }
}