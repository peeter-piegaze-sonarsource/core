package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.OfferPricePlanDto;
import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.SubscriptionFeeDto;
import org.meveo.api.dto.TerminationFeeDto;
import org.meveo.api.dto.UsageChargeDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurrenceTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.UsageChgTemplateEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OfferPricePlanServiceApi extends BaseApi {

	@Inject
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TaxService taxService;

	@Inject
	private SellerService sellerService;

	@Inject
	private CalendarService calendarService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private CounterTemplateService<CounterTemplate> counterTemplateService;

	@Inject
	private ServiceUsageChargeTemplateService serviceUsageChargeTemplateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	public void create(OfferPricePlanDto offerPricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(offerPricePlanDto.getOfferId())
				&& !StringUtils.isBlank(offerPricePlanDto.getOrganizationId())
				&& !StringUtils.isBlank(offerPricePlanDto.getTaxId())
				&& !StringUtils.isBlank(offerPricePlanDto.getBillingPeriod())) {

			Provider provider = providerService.findById(offerPricePlanDto
					.getProviderId());
			User currentUser = userService.findById(offerPricePlanDto
					.getCurrentUserId());

			Calendar calendar = calendarService.findByName(em,
					offerPricePlanDto.getBillingPeriod().toString());
			if (calendar == null) {
				throw new MeveoApiException("Calendar with name="
						+ offerPricePlanDto.getBillingPeriod()
						+ " does not exists.");
			}
			Seller seller = sellerService.findByCode(em,
					offerPricePlanDto.getOrganizationId(), provider);

			// get invoice sub category
			Tax tax = taxService.findByCode(em, offerPricePlanDto.getTaxId());
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryCountryService
					.findByTaxId(em, tax).getInvoiceSubCategory();

			// Create a charged service for defined offer and organization.
			// Service code is '_OF_[OrganizationId]_[OferId]'. Prefix '_OF_'
			// must be settable in properties file.
			String offerPricePlanPrefix = paramBean.getProperty(
					"asg.api.offer.offer.prefix", "_OF_");

			String serviceTemplateCode = offerPricePlanPrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId();

			// check if template exists
			if (serviceTemplateService.findByCode(em, serviceTemplateCode,
					provider) != null) {
				throw new MeveoApiException("Service template with code="
						+ serviceTemplateCode + " already exists.");
			}

			ServiceTemplate chargedServiceTemplate = new ServiceTemplate();
			chargedServiceTemplate.setCode(offerPricePlanPrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			chargedServiceTemplate.setActive(true);
			serviceTemplateService.create(em, chargedServiceTemplate,
					currentUser, provider);

			// Create a recurring charge with associated services and
			// parameters. Charge code is'_RE_OF_[OrganizationId]_[OfferId]'
			// ('_RE_OF_' must be settable). Charge is associate to step 1
			// service.
			String recurringChargePrefix = paramBean.getProperty(
					"asg.api.offer.recurring.prefix", "_RE_OF_");
			RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
			recurringChargeTemplate.setActive(true);
			recurringChargeTemplate.setCode(recurringChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			recurringChargeTemplate
					.setRecurrenceType(RecurrenceTypeEnum.CALENDAR);
			recurringChargeTemplate.setSubscriptionProrata(offerPricePlanDto
					.getSubscriptionProrata());
			recurringChargeTemplate.setTerminationProrata(offerPricePlanDto
					.getTerminationProrata());
			recurringChargeTemplate.setApplyInAdvance(offerPricePlanDto
					.getApplyInAdvance());
			recurringChargeTemplate.setType(OperationTypeEnum.CREDIT);
			recurringChargeTemplate.setCalendar(calendar);
			recurringChargeTemplateService.create(em, recurringChargeTemplate,
					currentUser, provider);

			// create price plans
			if (offerPricePlanDto.getRecurringCharges() != null
					&& offerPricePlanDto.getRecurringCharges().size() > 0) {
				for (RecurringChargeDto recurringChargeDto : offerPricePlanDto
						.getRecurringCharges()) {
					TradingCurrency tradingCurrency = tradingCurrencyService
							.findByTradingCurrencyCode(
									recurringChargeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(recurringChargeTemplate
							.getCode());
					pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(recurringChargeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(recurringChargeDto
							.getEndDate());
					pricePlanMatrix.setMinSubscriptionAgeInMonth(Long
							.valueOf(recurringChargeDto.getMinAge()));
					pricePlanMatrix.setMaxSubscriptionAgeInMonth(Long
							.valueOf(recurringChargeDto.getMaxAge()));
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create a subscription one point charge. Charge code
			// is'_SO_OF_[OrganizationId]_[OfferId]' ('_SO_OF_' must be
			// settable). Charge is associate to step 1 service.
			String subscriptionPointChargePrefix = paramBean
					.getProperty(
							"asg.api.offer.subscription.point.charge.prefix",
							"_SO_OF_");
			OneShotChargeTemplate subscriptionTemplate = new OneShotChargeTemplate();
			subscriptionTemplate.setActive(true);
			subscriptionTemplate.setCode(subscriptionPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			subscriptionTemplate.setInvoiceSubCategory(invoiceSubCategory);
			subscriptionTemplate
					.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.SUBSCRIPTION);
			oneShotChargeTemplateService.create(em, subscriptionTemplate,
					currentUser, provider);

			if (offerPricePlanDto.getSubscriptionFees() != null
					&& offerPricePlanDto.getSubscriptionFees().size() > 0) {
				for (SubscriptionFeeDto subscriptionFeeDto : offerPricePlanDto
						.getSubscriptionFees()) {
					TradingCurrency tradingCurrency = tradingCurrencyService
							.findByTradingCurrencyCode(
									subscriptionFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix
							.setEventCode(subscriptionTemplate.getCode());
					pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(subscriptionFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(subscriptionFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create e termination point charge. Charge code is
			// '_TE_OF_[OrganizationId]_[OfferId]' ('_TE_OF_' must be settable).
			// Charge is associate to step 1 service.
			String terminationPointChargePrefix = paramBean.getProperty(
					"asg.api.offer.termination.point.charge.prefix", "_TE_OF_");
			OneShotChargeTemplate terminationTemplate = new OneShotChargeTemplate();
			terminationTemplate.setActive(true);
			terminationTemplate.setCode(terminationPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			terminationTemplate.setInvoiceSubCategory(invoiceSubCategory);
			terminationTemplate
					.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.TERMINATION);
			oneShotChargeTemplateService.create(em, terminationTemplate,
					currentUser, provider);

			if (offerPricePlanDto.getTerminationFees() != null
					&& offerPricePlanDto.getTerminationFees().size() > 0) {
				for (TerminationFeeDto terminationFeeDto : offerPricePlanDto
						.getTerminationFees()) {
					TradingCurrency tradingCurrency = tradingCurrencyService
							.findByTradingCurrencyCode(
									terminationFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(terminationTemplate.getCode());
					pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(terminationFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(terminationFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			for (UsageChargeDto usageChargeDto : offerPricePlanDto
					.getUsageCharges()) {
				// Create a counter for each min range values used as
				// parameters.
				// Counters codes are '_SE_[OrganizationId]_[ServiceId]_[Valeur
				// Min]' ('_SE_' must be settable). Counters are ordered by
				// values.
				CounterTemplate counterTemplate = new CounterTemplate();
				counterTemplate.setCode(offerPricePlanPrefix
						+ offerPricePlanDto.getOrganizationId() + "_"
						+ offerPricePlanDto.getOfferId() + "_"
						+ usageChargeDto.getMin());
				counterTemplate.setCounterType(CounterTypeEnum.QUANTITY);
				counterTemplate.setCalendar(calendar);
				counterTemplate.setUnityDescription(offerPricePlanDto
						.getUsageUnit());
				Integer min = 0;
				if (usageChargeDto.getMin() != null) {
					min = usageChargeDto.getMin();
				}
				Integer max = null;
				if (usageChargeDto.getMax() != null) {
					max = usageChargeDto.getMax();
				}
				if (max != null) {
					counterTemplate.setLevel(new BigDecimal(max - min));
				}
				counterTemplateService.create(em, counterTemplate, currentUser,
						provider);

				// Create an usage charge for each counter. Charges codes are
				// '_US_SE_[OrganizationId]_[ServiceId]_[Valeur Min]' ('_US_SE_'
				// must be settable). This charge must be associated to step 1
				// service
				String usageChargeTemplatePrefix = paramBean.getProperty(
						"asg.api.offer.usage.charged.prefix", "_US_OF_");
				UsageChargeTemplate usageChargeTemplate = new UsageChargeTemplate();
				usageChargeTemplate.setCode(usageChargeTemplatePrefix
						+ offerPricePlanDto.getOrganizationId() + "_"
						+ offerPricePlanDto.getOfferId() + "_" + min);
				usageChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
				usageChargeTemplate
						.setUnityFormatter(UsageChgTemplateEnum.INTEGER);
				usageChargeTemplate.setUnityDescription(offerPricePlanDto
						.getUsageUnit());
				usageChargeTemplate.setPriority(min);
				usageChargeTemplateService.create(em, usageChargeTemplate,
						currentUser, provider);

				ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
				serviceUsageChargeTemplate
						.setChargeTemplate(usageChargeTemplate);
				serviceUsageChargeTemplate.setCounterTemplate(counterTemplate);
				serviceUsageChargeTemplate
						.setServiceTemplate(chargedServiceTemplate);
				serviceUsageChargeTemplateService.create(em,
						serviceUsageChargeTemplate, currentUser, provider);

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								usageChargeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
				pricePlanMatrix.setEventCode(usageChargeTemplate.getCode());
				pricePlanMatrix.setAmountWithoutTax(usageChargeDto.getPrice());
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
				pricePlanMatrix.setStartRatingDate(usageChargeDto
						.getStartDate());
				pricePlanMatrix.setSeller(seller);
				pricePlanMatrix.setEndRatingDate(usageChargeDto.getEndDate());
				pricePlanMatrix
						.setCriteria1Value(offerPricePlanDto.getParam1());
				pricePlanMatrix
						.setCriteria2Value(offerPricePlanDto.getParam2());
				pricePlanMatrix
						.setCriteria3Value(offerPricePlanDto.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}

			chargedServiceTemplate.getRecurringCharges().add(
					recurringChargeTemplate);
			chargedServiceTemplate.getSubscriptionCharges().add(
					subscriptionTemplate);
			chargedServiceTemplate.getTerminationCharges().add(
					terminationTemplate);
			serviceTemplateService.update(em, chargedServiceTemplate,
					currentUser);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(offerPricePlanDto.getOfferId())) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getBillingPeriod())) {
				missingFields.add("billingPeriod");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getTaxId())) {
				missingFields.add("taxId");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

}
