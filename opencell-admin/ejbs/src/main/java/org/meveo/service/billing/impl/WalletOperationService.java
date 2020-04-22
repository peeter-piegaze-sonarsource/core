/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DatePeriod;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.RatingResult;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * Service class for WalletOperation entity
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Phung tien lan
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class WalletOperationService extends PersistenceService<WalletOperation> {

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private RatingService ratingService;

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private WalletService walletService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private SellerService sellerService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    public WalletOperation applyOneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Date applicationDate, boolean isVirtual,
            String orderNumberOverride) throws BusinessException, RatingException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        if (applicationDate == null) {
            applicationDate = new Date();
        }

        log.debug("WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, multiplicator={}, applicationDate={}, chargeInstance.id={}, chargeInstance.desc={}", new Object[] { subscription.getId(),
                quantityInChargeUnits, chargeTemplateService.evaluateUnitRating(chargeInstance.getChargeTemplate()), applicationDate, chargeInstance.getId(), chargeInstance.getDescription() });

        RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, applicationDate, inputQuantity, quantityInChargeUnits, orderNumberOverride, null, null, null, null, false, isVirtual);

        WalletOperation walletOperation = ratingResult.getWalletOperation();

        if (isVirtual) {
            return walletOperation;
        }

        chargeWalletOperation(walletOperation);

        OneShotChargeTemplate oneShotChargeTemplate = null;

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        if (chargeTemplate instanceof OneShotChargeTemplate) {
            oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
        } else {
            oneShotChargeTemplate = oneShotChargeTemplateService.findById(chargeTemplate.getId());
        }

        Boolean immediateInvoicing = (oneShotChargeTemplate != null && oneShotChargeTemplate.getImmediateInvoicing() != null) ? oneShotChargeTemplate.getImmediateInvoicing() : false;

        if (immediateInvoicing != null && immediateInvoicing) {
            BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
            int delay = billingAccount.getBillingCycle().getInvoiceDateDelay();
            Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
            nextInvoiceDate = DateUtils.setTimeToZero(nextInvoiceDate);
            applicationDate = DateUtils.setTimeToZero(applicationDate);

            if (nextInvoiceDate == null || applicationDate.after(nextInvoiceDate)) {
                billingAccount.setNextInvoiceDate(applicationDate);
                billingAccountService.update(billingAccount);
            }
        }
        applyAccumulatorCounter(chargeInstance, Collections.singletonList(walletOperation), isVirtual);
        return walletOperation;
    }

    /**
     * Determine recurring period start date
     * 
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period start date
     */
    public Date getRecurringPeriodStartDate(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date previousChargeDate = cal.previousCalendarDate(cal.truncateDateTime(date));
        return previousChargeDate;
    }

    /**
     * Determine recurring period end date
     * 
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period end date
     */
    public Date getRecurringPeriodEndDate(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date nextChargeDate = cal.nextCalendarDate(cal.truncateDateTime(date));
        return nextChargeDate;
    }

    /**
     * Determine recurring period star and end dates
     * 
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period
     */
    public DatePeriod getRecurringPeriod(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date startPeriodDate = cal.previousCalendarDate(cal.truncateDateTime(date));
        Date endPeriodDate = cal.nextCalendarDate(cal.truncateDateTime(date));

        return new DatePeriod(startPeriodDate, endPeriodDate);
    }

    private Calendar resolveCalendar(RecurringChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        Calendar cal = chargeInstance.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
        }
        return cal;
    }

    /**
     * Determine if charge should be applied in advance
     * 
     * @param recurringChargeInstance Recurring charge instance
     * @return True if charge is applied in advance
     */
    public boolean isApplyInAdvance(RecurringChargeInstance recurringChargeInstance) {
        boolean isApplyInAdvance = (recurringChargeInstance.getApplyInAdvance() == null) ? false : recurringChargeInstance.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl(), recurringChargeInstance.getServiceInstance(),
                recurringChargeInstance.getRecurringChargeTemplate());
        }

        return isApplyInAdvance;
    }

    private boolean shouldNotIncludePPM(Date applyChargeOnDate, Date nextChargeDate, Date from, Date to) {
        return (to != null && (to.before(applyChargeOnDate) || to.getTime() == applyChargeOnDate.getTime())) || (from != null && (from.after(nextChargeDate) || from.getTime() == nextChargeDate.getTime()));
    }

    public boolean isChargeMatch(ChargeInstance chargeInstance, String filterExpression) throws BusinessException {
        if (StringUtils.isBlank(filterExpression)) {
            return true;
        }

        return ValueExpressionWrapper.evaluateToBooleanOneVariable(filterExpression, "ci", chargeInstance);
    }

    /**
     * Apply a recurring charge.<br>
     * Quantity might be prorated for first time charge (identified by chargeInstance.chargeDate=null) if subscription prorata is enabled on charge template. Will update charge
     * instance with a new charge and next charge dates. <br>
     * <br>
     * Recurring charge can be of two scenarious, depending on chargeInstance.applyInAdvance flag of its EL expression:
     * <ul>
     * <li>Apply charge that is applied at the end of calendar period - for charge instance with appliedInAdvance = false</li>
     * <li>Apply the recurring charge in advance of calendar period - for charge instance with appliedInAdvance = true</li>
     * </ul>
     * 
     * <b>Apply charge that is applied at the end of calendar period</b> applyInAdvance = false:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.nextChargeDate, wo.startDate = chargeInstance.chargeDate and
     * wo.endDate=chargeInstance.nextChargeDate.<br>
     * <br>
     * <b>Apply the recurring charge in advance of calendar period</b> applyInAdvance = true:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.chargeDate, wo.startDate = chargeInstance.chargeDate and wo.endDate=chargeInstance.nextChargeDate <br>
     * ---<br>
     * For non-reimbursement it will charge only the next calendar period cycle unless an explicit chargeToDate is provided. In such case last period might be prorated.<br>
     * For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus it might span multiple calendar periods
     * with first period being .<br>
     * ---<br>
     * It will also update chargeInstance.chargeDate = chargeInstance.nextChargeDate and chargeInstance.nextChargeDate = nextCalendarDate(chargeInstance.nextChargeDate)
     * 
     * 
     * @param chargeInstance Charge instance
     * @param chargeMode Charge application mode
     * @param forSchedule Is this a scheduled charge
     * @param proratePartialPeriods Should partial periods be prorated. Considered in case of reimbursement or end agreement type charging.
     * @param chargeToDate An explicit date to charge to. Assumption - to reach end agreement date. Optional. If provided, will charge up to a given date, pro-rating, if needed,
     *        the last period.
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param orderNumberToOverride Order number to assign to Wallet operation. Optional. If provided, will override a value from chargeInstance.
     * @return List of created wallet operations
     * @throws BusinessException General business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyReccuringCharge(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, boolean proratePartialPeriods, Date chargeToDate,
            String orderNumberToOverride, boolean isVirtual) throws BusinessException, RatingException {

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

        Date applyChargeFromDate = null;
        Date applyChargeToDate = null;

        boolean prorateFirstPeriod = false;
        Date prorateFirstPeriodFromDate = null;

        CounterPeriod firstChargeCounterPeriod = null;

        // -- Determine charge period, prorating for the first termination period and prorating of first subscription period

        // For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged.
        // This might span multiple calendar periods and might require a first period proration
        if (chargeMode == ChargeApplicationModeEnum.REIMBURSMENT) {

            if (chargeInstance.getChargedToDate() == null) {
                // Trying to reimburse something that was not charged yet
                log.error("Trying to reimburse a charge {} that was not charged yet. Will skip.", chargeInstance.getId());
                return new ArrayList<>();
            }

            applyChargeFromDate = chargeInstance.getTerminationDate();
            applyChargeToDate = chargeInstance.getChargedToDate();

            // Determine if first period proration is needed and is requested
            prorateFirstPeriodFromDate = getRecurringPeriodStartDate(chargeInstance, applyChargeFromDate);
            if (prorateFirstPeriodFromDate.before(applyChargeFromDate)) {
                prorateFirstPeriod = proratePartialPeriods;
            } else {
                prorateFirstPeriod = false;
                prorateFirstPeriodFromDate = null;
            }

            // For non-reimbursement it will cover only one calendar period cycle unless an explicit chargeToDate is specified.
            // In such case this might span multiple calendar periods and might require a last period proration
            // Initialize charge and determine prorata ratio if applying a charge for the first time
        } else {

            applyChargeFromDate = chargeInstance.getChargedToDate();

            boolean isFirstCharge = false;

            // First time charge
            if (applyChargeFromDate == null) {
                applyChargeFromDate = chargeInstance.getSubscriptionDate();
                isFirstCharge = true;
            }

            DatePeriod period = getRecurringPeriod(chargeInstance, applyChargeFromDate);
            applyChargeToDate = period.getTo();

            // When charging first time, need to determine if counter is available and prorata ratio if subscription charge proration is enabled
            if (isFirstCharge) {

                CounterInstance counterInstance = chargeInstance.getCounter();
                if (!isVirtual && counterInstance != null) {
                    boolean isApplyInAdvance = isApplyInAdvance(chargeInstance);
                    // get the counter period of recurring charge instance
                    CounterPeriod counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, chargeInstance.getChargeDate());

                    // If the counter is equal to 0, then the charge is not applied (but next activation date is updated).
                    if (counterPeriod != null && BigDecimal.ZERO.equals(counterPeriod.getValue())) {
                        chargeInstance.advanceChargeDates(applyChargeFromDate, applyChargeToDate, isApplyInAdvance ? applyChargeToDate : applyChargeFromDate);
                        return new ArrayList<>();

                    } else if (counterPeriod == null) {
                        counterPeriod = counterInstanceService.getOrCreateCounterPeriod(counterInstance, chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance,
                            chargeInstance.getServiceInstance());
                    }

                    firstChargeCounterPeriod = counterPeriod;
                }

                // Determine is subscription charge should be prorated
                prorateFirstPeriodFromDate = period.getFrom();
                if (prorateFirstPeriodFromDate.before(applyChargeFromDate)) {

                    boolean prorateSubscription = recurringChargeTemplate.getSubscriptionProrata() == null ? false : recurringChargeTemplate.getSubscriptionProrata();
                    if (!StringUtils.isBlank(recurringChargeTemplate.getSubscriptionProrataEl())) {
                        prorateSubscription = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getSubscriptionProrataEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
                    }

                    prorateFirstPeriod = prorateSubscription;

                } else {
                    prorateFirstPeriod = false;
                    prorateFirstPeriodFromDate = null;
                }
            }

            applyChargeToDate = chargeToDate != null ? chargeToDate : applyChargeToDate;
        }

        if (applyChargeFromDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        log.debug("Will apply {} recuring charges for charge {} for period(s) {} - {}.", chargeMode == ChargeApplicationModeEnum.REIMBURSMENT ? "reimbursement" : "", chargeInstance.getId(), applyChargeFromDate,
            applyChargeToDate);

        // -- Divide a period to charge into periods (or partial periods) and create WOs

        List<WalletOperation> walletOperations = new ArrayList<>();
        Date currentPeriodFromDate = applyChargeFromDate;
        int periodIndex = 0;

        while (applyChargeToDate != null && currentPeriodFromDate.getTime() < applyChargeToDate.getTime()) {

            boolean isApplyInAdvance = isApplyInAdvance(chargeInstance);
            boolean prorate = false;

            // Check if prorating is needed on first period of termination or on subscription
            Date effectivePeriodFromDate = currentPeriodFromDate;
            if (periodIndex == 0 && prorateFirstPeriodFromDate != null) {
                currentPeriodFromDate = prorateFirstPeriodFromDate;
                prorate = true && prorateFirstPeriod;
            }

            // Check if prorating is needed on last period
            Date currentPeriodToDate = getRecurringPeriodEndDate(chargeInstance, currentPeriodFromDate);
            Date effectivePeriodToDate = currentPeriodToDate;
            if (chargeToDate != null && currentPeriodToDate.after(chargeToDate)) {
                effectivePeriodToDate = chargeToDate;
                prorate = true && proratePartialPeriods;
            }

            // TODO Check how to handle prorating at the last period

            // If charge is not applicable for current period, skip it
            if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
                log.debug("IPIEL: not rating chargeInstance with id={}, filter expression evaluated to FALSE", chargeInstance.getId());

            } else {

                BigDecimal inputQuantity = chargeMode == ChargeApplicationModeEnum.REIMBURSMENT ? chargeInstance.getQuantity().negate() : chargeInstance.getQuantity();

                // Apply prorating if needed
                if (prorate) {
                    BigDecimal prorata = DateUtils.calculateProrataRatio(effectivePeriodFromDate, effectivePeriodToDate, currentPeriodFromDate, currentPeriodToDate, false);
                    if (prorata == null) {
                        throw new BusinessException("Failed to calculate prorating for charge id=" + chargeInstance.getId() + " : periodFrom=" + currentPeriodFromDate + ", periodTo=" + currentPeriodToDate
                                + ", proratedFrom=" + effectivePeriodFromDate + ", proratedTo=" + effectivePeriodToDate);
                    }

                    inputQuantity = inputQuantity.multiply(prorata);
                }

                log.debug("Applying {} recurring charge {} for period {} - {}, quantity {}",
                    chargeMode == ChargeApplicationModeEnum.REIMBURSMENT ? "reimbursement" : isApplyInAdvance ? "start of period" : "end of period", chargeInstance.getId(), effectivePeriodFromDate, effectivePeriodToDate,
                    inputQuantity);

                if (recurringChargeTemplate.isProrataOnPriceChange()) {
                    walletOperations.addAll(generateWalletOperationsByPricePlan(chargeInstance, chargeMode, forSchedule, effectivePeriodFromDate, effectivePeriodToDate, inputQuantity,
                        orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), isApplyInAdvance, isVirtual));

                } else {
                    RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, isApplyInAdvance ? effectivePeriodFromDate : effectivePeriodToDate, inputQuantity, null,
                        orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), effectivePeriodFromDate, effectivePeriodToDate, chargeMode, null, forSchedule, false);

                    WalletOperation walletOperation = ratingResult.getWalletOperation();
                    walletOperation.setSubscriptionDate(chargeInstance.getSubscriptionDate());
                    if (prorate) {
                        walletOperation.setFullRatingPeriod(new DatePeriod(currentPeriodFromDate, currentPeriodToDate));
                    }

                    if (forSchedule) {
                        walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                    }

                    if (isVirtual) {
                        walletOperations.add(walletOperation);
                    } else {
                        List<WalletOperation> operations = chargeWalletOperation(walletOperation);
                        walletOperations.addAll(operations);
                    }
                }
                if (!isVirtual && chargeMode != ChargeApplicationModeEnum.REIMBURSMENT) {
                    applyAccumulatorCounter(chargeInstance, walletOperations, false);
                }
            }

            // TODO this should happen before the rating, so rating script can change the nextChargeDate value
            if (isApplyInAdvance) {
                chargeInstance.advanceChargeDates(effectivePeriodFromDate, effectivePeriodToDate, effectivePeriodToDate);
            } else {
                chargeInstance.advanceChargeDates(effectivePeriodToDate, getRecurringPeriodEndDate(chargeInstance, effectivePeriodToDate), effectivePeriodToDate);
            }
            currentPeriodFromDate = currentPeriodToDate;
            periodIndex++;
        }

        // The counter will be decremented by charge quantity
        if (!isVirtual && firstChargeCounterPeriod != null) {
            CounterValueChangeInfo counterValueChangeInfo = counterInstanceService.deduceCounterValue(firstChargeCounterPeriod, chargeInstance.getQuantity(), false);
            applyAccumulatorCounter(chargeInstance, walletOperations, false);
            counterInstanceService.triggerCounterPeriodEvent(counterValueChangeInfo, firstChargeCounterPeriod);
        }

        return walletOperations;
    }

    // TODO AKK what if rateCharge would return multiple WOs as alternative to this method??
    private List<WalletOperation> generateWalletOperationsByPricePlan(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date periodStartDate, Date periodEndDate,
            BigDecimal inputQuantity, String orderNumberToOverride, boolean isApplyInAdvance, boolean isVirtual) {

        String recurringChargeTemplateCode = chargeInstance.getRecurringChargeTemplate().getCode();

        return ratingService.getActivePricePlansByChargeCode(recurringChargeTemplateCode).stream()
            .filter(ppm -> !shouldNotIncludePPM(periodStartDate, periodEndDate, ppm.getValidityFrom(), ppm.getValidityDate()) && (ppm.getValidityFrom() != null && periodEndDate.after(ppm.getValidityFrom())))
            .map(pricePlanMatrix -> {

                Date computedApplyChargeOnDate = pricePlanMatrix.getValidityFrom().after(periodStartDate) ? pricePlanMatrix.getValidityFrom() : periodStartDate;
                Date computedNextChargeDate = pricePlanMatrix.getValidityDate() != null && pricePlanMatrix.getValidityDate().before(periodEndDate) ? pricePlanMatrix.getValidityDate() : periodEndDate;
                double ratio = computeQuantityRatio(computedApplyChargeOnDate, computedNextChargeDate);
                BigDecimal computedInputQuantityHolder = inputQuantity.multiply(new BigDecimal(ratio + ""));

                RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, isApplyInAdvance ? computedApplyChargeOnDate : computedNextChargeDate, computedInputQuantityHolder, null,
                    orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), computedApplyChargeOnDate, computedNextChargeDate, chargeMode, null, forSchedule, false);

                WalletOperation walletOperation = ratingResult.getWalletOperation();
                walletOperation.setSubscriptionDate(chargeInstance.getSubscriptionDate());

                if (forSchedule) {
                    walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                }
                if (isVirtual) {
                    return Arrays.asList(walletOperation);

                } else {
                    return chargeWalletOperation(walletOperation);
                }

            }).flatMap(List::stream).collect(Collectors.toList());
    }

    public void applyAccumulatorCounter(ChargeInstance chargeInstance, List<WalletOperation> walletOperations, boolean isVirtual) {

        for (CounterInstance counterInstance : chargeInstance.getCounterInstances()) {
            CounterPeriod counterPeriod = null;
            if (counterInstance != null) {
                // get the counter period of charge instance
                log.debug("Get accumulator counter period for counter instance {}", counterInstance);
                counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, chargeInstance.getChargeDate());
                if (counterPeriod == null || counterPeriod.getValue() == null || !counterPeriod.getValue().equals(BigDecimal.ZERO)) {
                    // The counter will be incremented by charge quantity
                    if (counterPeriod == null) {
                        counterPeriod = counterInstanceService.getOrCreateCounterPeriod(counterInstance, chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance,
                            chargeInstance.getServiceInstance());
                    }
                }

                for (WalletOperation wo : walletOperations) {
                    log.debug("Increment accumulator counter period value {} by the WO's amount {} or quantity {} ", counterPeriod, wo.getAmountWithoutTax(), wo.getQuantity());
                    counterInstanceService.accumulatorCounterPeriodValue(counterPeriod, wo, null, isVirtual);
                }
            }
        }
    }

    private double computeQuantityRatio(Date computedApplyChargeOnDate, Date computedNextChargeDate) {
        int lengthOfMonth = 30;
        double days = DateUtils.daysBetween(computedApplyChargeOnDate, computedNextChargeDate);
        return days > lengthOfMonth ? 1 : days / lengthOfMonth;
    }

    /**
     * Get a list of wallet operations to rate up to a given date. WalletOperation.invoiceDate< date
     *
     * @param entityToInvoice Entity to invoice
     * @param invoicingDate Invoicing date
     * @return A list of wallet operations
     */
    public List<WalletOperation> listToRate(IBillableEntity entityToInvoice, Date invoicingDate) {

        if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByBA", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("billingAccount", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateBySubscription", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("subscription", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByOrderNumber", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber()).getResultList();
        }

        return new ArrayList<>();
    }

    /**
     * Get a list of wallet operations to be invoiced/converted to rated transactions up to a given date. WalletOperation.invoiceDate< date
     * 
     * @param invoicingDate Invoicing date
     * @param nbToRetrieve Number of items to retrieve for processing
     * @return A list of Wallet operation ids
     */
    public List<Long> listToRate(Date invoicingDate, int nbToRetrieve) {
        return getEntityManager().createNamedQuery("WalletOperation.listToRateIds", Long.class).setParameter("invoicingDate", invoicingDate).setMaxResults(nbToRetrieve).getResultList();
    }

    public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount) {

        try {
            return getEntityManager().createNamedQuery("WalletOperation.findByUAAndCode", WalletOperation.class).setParameter("userAccount", userAccount).setParameter("code", code).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Charge wallet operation on prepaid wallets
     * 
     * @param chargeInstance Charge instance
     * @param op Wallet operation
     * @return A list of wallet operations containing a single original wallet operation or multiple wallet operations if had to be split among various wallets
     * @throws BusinessException General business exception
     * @throws InsufficientBalanceException Balance is insufficient in the wallet
     */
    private List<WalletOperation> chargeOnPrepaidWallets(ChargeInstance chargeInstance, WalletOperation op) throws BusinessException, InsufficientBalanceException {

        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        List<WalletOperation> result = new ArrayList<>();
        Map<Long, BigDecimal> walletLimits = walletService.getWalletIds(chargeInstance);

        // Handles negative amounts (recharge) - apply recharge to the first wallet
        if (op.getAmountWithTax().compareTo(BigDecimal.ZERO) <= 0) {

            Long walletId = walletLimits.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);
            return result;
        }

        log.debug("chargeWalletOperation chargeInstanceId found with {} wallet ids", walletLimits.size());

        Map<Long, BigDecimal> balances = walletService.getWalletReservedBalances(walletLimits.keySet());

        Map<Long, BigDecimal> woAmounts = new HashMap<>();

        BigDecimal remainingAmountToCharge = op.getAmountWithTax();

        // First iterate over balances that have credit
        for (Long walletId : balances.keySet()) {

            BigDecimal balance = balances.get(walletId);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal negatedBalance = balance.negate();
                // Case when amount to deduct (5) is less than or equal to a negated balance amount -(-10)
                if (remainingAmountToCharge.compareTo(negatedBalance) <= 0) {
                    woAmounts.put(walletId, remainingAmountToCharge);
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Case when amount to deduct (10) is more tan a negated balance amount -(-5)
                } else {
                    woAmounts.put(walletId, negatedBalance);
                    balances.put(walletId, BigDecimal.ZERO);
                    remainingAmountToCharge = remainingAmountToCharge.add(balance);
                }
            }
        }

        // If not all the amount was deducted, then iterate again checking if any of the balances can be reduced pass the Zero up to a rejection limit as defined in a wallet.
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {

            for (Long walletId : balances.keySet()) {

                BigDecimal balance = balances.get(walletId);
                BigDecimal rejectLimit = walletLimits.get(walletId);

                // There is no limit upon which further consumption should be rejected
                if (rejectLimit == null) {
                    if (woAmounts.containsKey(walletId)) {
                        woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                    } else {
                        woAmounts.put(walletId, remainingAmountToCharge);
                    }
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Limit is not exceeded yet
                } else if (rejectLimit.compareTo(balance) > 0) {

                    BigDecimal remainingLimit = rejectLimit.subtract(balance);

                    // Case when amount to deduct (5) is less than or equal to a remaining limit (10)
                    if (remainingAmountToCharge.compareTo(remainingLimit) <= 0) {
                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                        } else {
                            woAmounts.put(walletId, remainingAmountToCharge);
                        }

                        balances.put(walletId, balance.add(remainingAmountToCharge));
                        remainingAmountToCharge = BigDecimal.ZERO;
                        break;

                        // Case when amount to deduct (10) is more tan a remaining limit (5)
                    } else {

                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingLimit));
                        } else {
                            woAmounts.put(walletId, remainingLimit);
                        }

                        balances.put(walletId, rejectLimit);
                        remainingAmountToCharge = remainingAmountToCharge.subtract(remainingLimit);
                    }
                }
            }
        }

        // Not possible to deduct all WO amount, so throw an Insufficient balance error
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientBalanceException("Insuficient balance when charging " + op.getAmountWithTax() + " for wallet operation " + op.getId());
        }

        // All charge was over one wallet
        if (woAmounts.size() == 1) {
            Long walletId = woAmounts.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);

            // Charge was over multiple wallets
        } else {

            for (Entry<Long, BigDecimal> amountInfo : woAmounts.entrySet()) {
                Long walletId = amountInfo.getKey();
                BigDecimal walletAmount = amountInfo.getValue();

                BigDecimal newOverOldCoeff = walletAmount.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal newOpAmountWithTax = walletAmount;
                BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);

                newOpAmountWithoutTax = round(newOpAmountWithoutTax, rounding, roundingMode);
                newOpAmountWithTax = round(newOpAmountWithTax, rounding, roundingMode);
                BigDecimal newOpAmountTax = newOpAmountWithTax.subtract(newOpAmountWithoutTax);
                BigDecimal newOpQuantity = op.getQuantity().multiply(newOverOldCoeff);

                WalletOperation newOp = op.getUnratedClone();
                newOp.setWallet(getEntityManager().find(WalletInstance.class, walletId));
                newOp.setAmountWithTax(newOpAmountWithTax);
                newOp.setAmountTax(newOpAmountTax);
                newOp.setAmountWithoutTax(newOpAmountWithoutTax);
                newOp.setQuantity(newOpQuantity);
                log.debug("prepaid walletoperation partially fit in walletInstance {}, we charge {} of  ", newOp.getWallet(), newOpAmountTax, op.getAmountWithTax());
                create(newOp);
                result.add(newOp);
                walletCacheContainerProvider.updateBalance(newOp);
            }
        }
        return result;
    }

    public List<WalletOperation> chargeWalletOperation(WalletOperation op) throws BusinessException, InsufficientBalanceException {

        List<WalletOperation> result = new ArrayList<>();
        ChargeInstance chargeInstance = op.getChargeInstance();
        Long chargeInstanceId = chargeInstance.getId();
        // case of scheduled operation (for revenue recognition)
        UserAccount userAccount = chargeInstance.getUserAccount();

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        if (chargeTemplate != null) {
            if (op.getInputUnitDescription() == null) {
                op.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
            }
            if (op.getRatingUnitDescription() == null) {
                op.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
            }
            if (op.getInvoiceSubCategory() == null) {
                op.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
            }
        }

        if (chargeInstanceId == null) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Balance and reserved balance deals with prepaid wallets. If charge instance does not contain any prepaid wallet, then it is a postpaid charge and dont need to deal
            // with wallet cache at all
        } else if (!chargeInstance.getPrepaid()) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Prepaid charges only
        } else {
            result = chargeOnPrepaidWallets(chargeInstance, op);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int updateToRerate(List<Long> walletIdList) {
        int walletsOpToRerate = 0;

        // Ignore Rated transactions that were billed already
        // TODO AKK check if RT table can be excluded and join is made directly between WOstatus and RTstatus tables
        List<Long> walletOperationsBilled = (List<Long>) getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled").setParameter("walletIdList", walletIdList).getResultList();
        walletIdList.removeAll(walletOperationsBilled);

        if (!walletIdList.isEmpty()) {
            // cancelled selected rts
            getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("notBilledWalletIdList", walletIdList).executeUpdate();
            // set selected wo to rerate and ratedTx.id=null
            walletsOpToRerate = getEntityManager().createNamedQuery("WalletOperation.setStatusToRerate").setParameter("now", new Date()).setParameter("notBilledWalletIdList", walletIdList).executeUpdate();

        }
        getEntityManager().flush();
        return walletsOpToRerate;
    }

    public List<Long> listToRerate() {
        return (List<Long>) getEntityManager().createNamedQuery("WalletOperation.listToRerate", Long.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder("Select op from WalletOperation op", "op");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("op.wallet", walletInstance);
        qb.addSql(" op.status = 'OPEN'");
        if (from != null) {
            qb.addCriterion("operationDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("operationDate", "<=", to, false);
        }

        try {
            return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> openWalletOperationsByCharge(WalletInstance walletInstance) {

        try {
            // todo ejbQL and make namedQuery
            List<Object[]> resultList = getEntityManager().createNativeQuery("select op.description ,sum(op.quantity) QT, sum(op.amount_without_tax) MT ,op.input_unit_description from "
                    + "billing_wallet_operation op , cat_charge_template ct, billing_charge_instance ci " + "where op.wallet_id = " + walletInstance.getId()
                    + " and  op.status = 'OPEN'  and op.charge_instance_id = ci.id and ci.charge_template_id = ct.id and ct.id in (select id from cat_usage_charge_template) "
                    + "group by op.description, op.input_unit_description")
                .getResultList();

            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long countNonTreatedWOByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by BA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by UA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by CA", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrWalletsOperationByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("WalletOperation.countNbrWalletsOperationByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrWalletsOperationByStatus", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrEdrByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("EDR.countNbrEdrByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrEdrByStatus", e);
            return null;
        }
    }

    public List<AggregatedWalletOperation> listToInvoiceIdsWithGrouping(Date invoicingDate, RatedTransactionsJobAggregationSetting aggregationSettings) {

        WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings);

        String strQuery = woa.getGroupQuery();
        log.debug("aggregated query={}", strQuery);

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("invoicingDate", invoicingDate);

        // get the aggregated data
        @SuppressWarnings("unchecked")
        List<AggregatedWalletOperation> result = (List<AggregatedWalletOperation>) query.getResultList();

        return result;
    }

    public List<WalletOperation> listByRatedTransactionId(Long ratedTransactionId) {
        return getEntityManager().createNamedQuery("WalletOperation.listByRatedTransactionId", WalletOperation.class).setParameter("ratedTransactionId", ratedTransactionId).getResultList();
    }

    /**
     * Return a list of open Wallet operation between two date.
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @param lastId a last id for pagination
     * @param max a max rows
     * @return a list of Wallet Operation
     */
    public List<WalletOperation> getNotOpenedWalletOperationBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, Long lastId, int max) {
        return getEntityManager().createNamedQuery("WalletOperation.listNotOpenedWObetweenTwoDates", WalletOperation.class).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).setParameter("lastId", lastId).setMaxResults(max).getResultList();
    }

    /**
     * Remove all not open Wallet operation between two date
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {

        return getEntityManager().createNamedQuery("WalletOperation.deleteNotOpenWObetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .executeUpdate();
    }

    /**
     * Import wallet operations.
     * 
     * @param walletOperations Wallet Operations DTO list
     * @throws BusinessException
     */
    public void importWalletOperation(List<WalletOperationDto> walletOperations) throws BusinessException {

        for (WalletOperationDto dto : walletOperations) {
            Tax tax = null;
            ChargeInstance chargeInstance = null;

            if (dto.getTaxCode() != null) {
                tax = taxService.findByCode(dto.getTaxCode());
            } else if (dto.getTaxPercent() != null) {
                tax = taxService.findTaxByPercent(dto.getTaxPercent());
            }
            if (tax == null) {
                log.warn("No tax matched for wallet operation by code {} nor tax percent {}", dto.getTaxCode(), dto.getTaxPercent());
                continue;
            }

            if (dto.getChargeInstance() != null) {
                chargeInstance = (ChargeInstance) chargeInstanceService.findByCode(dto.getChargeInstance());
            }

            WalletOperation wo = null;
            if (chargeInstance != null) {
                wo = new WalletOperation(chargeInstance, dto.getQuantity(), null, dto.getOperationDate(), dto.getOrderNumber(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(), dto.getParameterExtra(), tax,
                    dto.getStartDate(), dto.getEndDate(), null);

            } else {
                Seller seller = null;
                WalletInstance wallet = null;
                Currency currency = null;
                OfferTemplate offer = null;

                if (dto.getOfferCode() != null) {
                    offer = offerTemplateService.findByCode(dto.getOfferCode());
                }

                if (dto.getSeller() != null) {
                    seller = sellerService.findByCode(dto.getSeller());
                }
                if (dto.getWalletId() != null) {
                    wallet = walletService.findById(dto.getWalletId());
                }
                if (dto.getCurrency() != null) {
                    currency = currencyService.findByCode(dto.getCurrency());
                }
                wo = new WalletOperation(dto.getCode(), "description", wallet, dto.getOperationDate(), null, dto.getType(), currency, tax, dto.getUnitAmountWithoutTax(), dto.getUnitAmountWithTax(),
                    dto.getUnitAmountTax(), dto.getQuantity(), dto.getAmountWithoutTax(), dto.getAmountWithTax(), dto.getAmountTax(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(),
                    dto.getParameterExtra(), dto.getStartDate(), dto.getEndDate(), dto.getSubscriptionDate(), offer, seller, null, dto.getRatingUnitDescription(), null, null, null, null, dto.getStatus());
            }

            create(wo);
        }
    }

    /**
     * Mark wallet operations, that were invoiced by a given billing run, to be rerated
     * 
     * @param billingRun Billing run that invoiced wallet operations
     */
    public void markToRerateByBR(BillingRun billingRun) {

        List<WalletOperation> walletOperations = getEntityManager().createNamedQuery("WalletOperation.listByBRId", WalletOperation.class).setParameter("brId", billingRun.getId()).getResultList();

        for (WalletOperation walletOperation : walletOperations) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TO_RERATE);
        }
    }

    /**
     * @param firstDate
     * @param lastDate
     * @param lastId
     * @param maxResult
     * @param formattedStatus
     * @return
     */
    public List<WalletOperation> getWalletOperationBetweenTwoDatesByStatus(Date firstDate, Date lastDate, Long lastId, int maxResult, List<WalletOperationStatusEnum> formattedStatus) {
        return getEntityManager().createNamedQuery("WalletOperation.listWObetweenTwoDatesByStatus", WalletOperation.class).setParameter("firstTransactionDate", firstDate).setParameter("lastTransactionDate", lastDate)
            .setParameter("lastId", lastId).setParameter("status", formattedStatus).setMaxResults(maxResult).getResultList();
    }

    public long purge(Date firstTransactionDate, Date lastTransactionDate, List<WalletOperationStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("WalletOperation.deleteWObetweenTwoDatesByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }

    /**
     * Remove wallet operation rated 0 and chargeTemplate.dropZeroWo=true.
     */
    public void removeZeroWalletOperation() {
        getEntityManager().createNamedQuery("WalletOperation.deleteZeroWO").executeUpdate();
    }
}