/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.billing.ProductInstanceDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderHistoryService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Stateless
public class SubscriptionService extends BusinessService<Subscription> {

	@Inject
	private OfferModelScriptService offerModelScriptService;

	@EJB
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private AccessService accessService;

	@Inject
	private OrderHistoryService orderHistoryService;

	@Inject
	private EntityToDtoConverter entityToDtoConverter;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private CustomEntityInstanceService customEntityInstanceService;

	@MeveoAudit
	@Override
	public void create(Subscription subscription) throws BusinessException {

		subscription.updateSubscribedTillAndRenewalNotifyDates();

		super.create(subscription);

		// execute subscription script
		if (subscription.getOffer().getBusinessOfferModel() != null
				&& subscription.getOffer().getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.subscribe(subscription,
						subscription.getOffer().getBusinessOfferModel().getScript().getCode());
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}",
						subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
			}
		}
	}

	@MeveoAudit
	@Override
	public Subscription update(Subscription subscription) throws BusinessException {

		subscription.updateSubscribedTillAndRenewalNotifyDates();

		return super.update(subscription);
	}

	@MeveoAudit
	public Subscription subscriptionCancellation(Subscription subscription, Date cancelationDate)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		if (cancelationDate == null) {
			cancelationDate = new Date();
		}
		/*
		 * List<ServiceInstance> serviceInstances = subscription .getServiceInstances();
		 * for (ServiceInstance serviceInstance : serviceInstances) { if
		 * (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
		 * serviceInstanceService.serviceCancellation(serviceInstance, terminationDate);
		 * } }
		 */
		subscription.setTerminationDate(cancelationDate);
		subscription.setStatus(SubscriptionStatusEnum.CANCELED);
		subscription = update(subscription);

		return subscription;
	}

	@MeveoAudit
	public Subscription subscriptionSuspension(Subscription subscription, Date suspensionDate)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		if (suspensionDate == null) {
			suspensionDate = new Date();
		}

		if (subscription.getOffer().getBusinessOfferModel() != null
				&& subscription.getOffer().getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.suspendSubscription(subscription,
						subscription.getOffer().getBusinessOfferModel().getScript().getCode(), suspensionDate);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}",
						subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
			}
		}

		List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceSuspension(serviceInstance, suspensionDate);
			}
		}

		subscription.setTerminationDate(suspensionDate);
		subscription.setStatus(SubscriptionStatusEnum.SUSPENDED);
		subscription = update(subscription);
		for (Access access : subscription.getAccessPoints()) {
			accessService.disable(access);
		}

		return subscription;
	}

	@MeveoAudit
	public Subscription subscriptionReactivation(Subscription subscription, Date reactivationDate)
			throws IncorrectSusbcriptionException, ElementNotResiliatedOrCanceledException,
			IncorrectServiceInstanceException, BusinessException {

		if (reactivationDate == null) {
			reactivationDate = new Date();
		}

		if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED
				&& subscription.getStatus() != SubscriptionStatusEnum.CANCELED
				&& subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
			throw new ElementNotResiliatedOrCanceledException("subscription", subscription.getCode());
		}

		subscription.setTerminationDate(null);
		subscription.setSubscriptionTerminationReason(null);
		subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

		List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceReactivation(serviceInstance, reactivationDate);
			}
		}

		subscription = update(subscription);

		for (Access access : subscription.getAccessPoints()) {
			accessService.enable(access);
		}

		if (subscription.getOffer().getBusinessOfferModel() != null
				&& subscription.getOffer().getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.reactivateSubscription(subscription,
						subscription.getOffer().getBusinessOfferModel().getScript().getCode(), reactivationDate);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}",
						subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
			}
		}

		return subscription;
	}

	@MeveoAudit
	public Subscription terminateSubscription(Subscription subscription, Date terminationDate,
			SubscriptionTerminationReason terminationReason, String orderNumber) throws BusinessException {
		return terminateSubscription(subscription, terminationDate, terminationReason, orderNumber, null, null);
	}

	@MeveoAudit
	public Subscription terminateSubscription(Subscription subscription, Date terminationDate,
			SubscriptionTerminationReason terminationReason, String orderNumber, Long orderItemId,
			OrderItemActionEnum orderItemAction) throws BusinessException {

		if (terminationReason == null) {
			throw new BusinessException("terminationReason is null");
		}

		return terminateSubscription(subscription, terminationDate, terminationReason,
				terminationReason.isApplyAgreement(), terminationReason.isApplyReimbursment(),
				terminationReason.isApplyTerminationCharges(), orderNumber, orderItemId, orderItemAction);
	}

	@MeveoAudit
	private Subscription terminateSubscription(Subscription subscription, Date terminationDate,
			SubscriptionTerminationReason terminationReason, boolean applyAgreement, boolean applyReimbursment,
			boolean applyTerminationCharges, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction)
			throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}

		List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())
					|| InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
				if (terminationReason != null) {
					serviceInstanceService.terminateService(serviceInstance, terminationDate, terminationReason,
							orderNumber);
				} else {
					serviceInstanceService.terminateService(serviceInstance, terminationDate, applyAgreement,
							applyReimbursment, applyTerminationCharges, orderNumber, null);
				}

				orderHistoryService.create(orderNumber, orderItemId, serviceInstance, orderItemAction);
			}
		}

		if (terminationReason != null) {
			subscription.setSubscriptionTerminationReason(terminationReason);
		}
		subscription.setTerminationDate(terminationDate);
		subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
		subscription = update(subscription);

		for (Access access : subscription.getAccessPoints()) {
			access.setEndDate(terminationDate);
			accessService.update(access);
		}

		// execute termination script
		if (subscription.getOffer().getBusinessOfferModel() != null
				&& subscription.getOffer().getBusinessOfferModel().getScript() != null) {
			offerModelScriptService.terminateSubscription(subscription,
					subscription.getOffer().getBusinessOfferModel().getScript().getCode(), terminationDate,
					terminationReason);
		}

		return subscription;
	}

	public boolean hasSubscriptions(OfferTemplate offerTemplate) {
		try {
			QueryBuilder qb = new QueryBuilder(Subscription.class, "s");
			qb.addCriterionEntity("offer", offerTemplate);

			return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult()).longValue() > 0;
		} catch (NoResultException e) {
			return false;
		}
	}

	public List<Subscription> listByUserAccount(UserAccount userAccount) {
		return listByUserAccount(userAccount, "code", SortOrder.ASCENDING);
	}

	@SuppressWarnings("unchecked")
	public List<Subscription> listByUserAccount(UserAccount userAccount, String sortBy, SortOrder sortOrder) {
		QueryBuilder qb = new QueryBuilder(Subscription.class, "c");
		qb.addCriterionEntity("userAccount", userAccount);
		boolean ascending = true;
		if (sortOrder != null) {
			ascending = sortOrder.equals(SortOrder.ASCENDING);
		}
		qb.addOrderCriterion(sortBy, ascending);

		try {
			return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("error while getting list subscription by user account", e);
			return null;
		}
	}

	/**
	 * Get a list of subscription ids that are about to expire or have expired
	 * already
	 * 
	 * @return A list of subscription ids
	 */
	public List<Long> getSubscriptionsToRenewOrNotify() {

		List<Long> ids = getEntityManager().createNamedQuery("Subscription.getExpired", Long.class)
				.setParameter("date", new Date())
				.setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED))
				.getResultList();
		ids.addAll(getEntityManager().createNamedQuery("Subscription.getToNotifyExpiration", Long.class)
				.setParameter("date", new Date())
				.setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED))
				.getResultList());

		return ids;
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInstance> listBySubscription(Subscription subscription) {
		QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
		qb.addCriterionEntity("subscription", subscription);

		try {
			return (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("error while getting user account list by billing account", e);
			return null;
		}
	}

	public List<SubscriptionDto> listWithCfs(List<Subscription> subscriptions, CustomFieldInheritanceEnum inheritCF) {

		List<SubscriptionDto> res = new ArrayList<SubscriptionDto>(subscriptions.size());

		Set<String> subscriptionsDistinctAtvs = new HashSet<String>();
		Set<String> accessPointsAtvs = new HashSet<String>();
		Set<String> serviceInstancesAtvs = new HashSet<String>();
		Set<String> productInstancesAtvs = new HashSet<String>();

		for (Subscription subscription : subscriptions) {

			try {

				String subscriptionAppliesToValue = CustomFieldTemplateService.calculateAppliesToValue(subscription);
				subscription.setCftAppliesTo(subscriptionAppliesToValue);
				subscriptionsDistinctAtvs.add(subscriptionAppliesToValue);

				List<Access> aps = subscription.getAccessPoints();
				if (aps != null) {
					for (Access ap : aps) {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(ap);
						ap.setCftAppliesTo(atv);
						accessPointsAtvs.add(atv);
					}
				}

				List<ServiceInstance> sis = subscription.getServiceInstances();
				if (sis != null) {
					for (ServiceInstance si : sis) {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(si);
						si.setCftAppliesTo(atv);
						serviceInstancesAtvs.add(atv);
					}
				}

				List<ProductInstance> pis = subscription.getProductInstances();
				if (pis != null) {
					for (ProductInstance pi : pis) {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(pi);
						pi.setCftAppliesTo(atv);
						productInstancesAtvs.add(atv);
					}
				}

			} catch (CustomFieldException e) {
				log.error("error while calculating cft applies to value", e.getLocalizedMessage(), e);
			}
		}

		Map<String, CustomFieldTemplate> subscriptionsCfts = null;
		Map<String, CustomFieldTemplate> accessPointsCfts = null;
		Map<String, CustomFieldTemplate> serviceInstancesCfts = null;
		Map<String, CustomFieldTemplate> productInstancesCfts = null;
		
		if(subscriptionsDistinctAtvs.size() > 0) {
			subscriptionsCfts = customFieldTemplateService.findByAppliesTo(subscriptionsDistinctAtvs);
		}
		
		if (accessPointsAtvs.size() > 0) {
			accessPointsCfts = customFieldTemplateService.findByAppliesTo(accessPointsAtvs);
		}
		
		if (serviceInstancesAtvs.size() > 0) {
			serviceInstancesCfts = customFieldTemplateService.findByAppliesTo(serviceInstancesAtvs);
		}

		if (productInstancesAtvs.size() > 0) {
			productInstancesCfts = customFieldTemplateService.findByAppliesTo(productInstancesAtvs);
		}

		
		for (Subscription subscription : subscriptions) {
			
			SubscriptionDto dto = new SubscriptionDto();
			dto.setCode(subscription.getCode());
			dto.setDescription(subscription.getDescription());
			dto.setStatus(subscription.getStatus());
			dto.setStatusDate(subscription.getStatusDate());
			dto.setOrderNumber(subscription.getOrderNumber());

			if (subscription.getUserAccount() != null) {
				dto.setUserAccount(subscription.getUserAccount().getCode());
			}

			if (subscription.getOffer() != null) {
				dto.setOfferTemplate(subscription.getOffer().getCode());
			}

			dto.setSubscriptionDate(subscription.getSubscriptionDate());
			dto.setTerminationDate(subscription.getTerminationDate());
			if (subscription.getSubscriptionTerminationReason() != null) {
				dto.setTerminationReason(subscription.getSubscriptionTerminationReason().getCode());
			}

			dto.setEndAgreementDate(subscription.getEndAgreementDate());
			dto.setSubscribedTillDate(subscription.getSubscribedTillDate());
			dto.setRenewed(subscription.isRenewed());
			dto.setRenewalNotifiedDate(subscription.getRenewalNotifiedDate());
			dto.setRenewalRule(new SubscriptionRenewalDto(subscription.getSubscriptionRenewal()));
			
			if (accessPointsCfts != null && subscription.getAccessPoints() != null) {

				List<Access> aps = subscription.getAccessPoints();
				for (Access ap : aps) {
					try {
						CustomFieldsDto apDto = getCustomFieldsDTO(inheritCF, accessPointsCfts, ap);
						AccessDto accessDto = new AccessDto(ap, apDto);
						dto.getAccesses().getAccess().add(accessDto);
					} catch (CustomFieldException e) {
						e.printStackTrace();
					}
				}

			}
			
			if (subscriptionsCfts != null) {
				try {
					dto.setCustomFields(getCustomFieldsDTO(inheritCF, subscriptionsCfts, subscription));
				} catch (CustomFieldException e) {
					e.printStackTrace();
				}
			}
			
			if (serviceInstancesCfts != null) {
				try {
					if (subscription.getServiceInstances() != null) {
						for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
							ServiceInstanceDto serviceInstanceDto = null;
							CustomFieldsDto customFieldsDTO = null;
							customFieldsDTO = getCustomFieldsDTO(inheritCF, serviceInstancesCfts, serviceInstance);
							serviceInstanceDto = new ServiceInstanceDto(serviceInstance, customFieldsDTO);
							dto.getServices().addServiceInstance(serviceInstanceDto);
						}
					}
				} catch (CustomFieldException e) {
					e.printStackTrace();
				}
			}
			
			if(productInstancesCfts != null) {
				try {
				 if (subscription.getProductInstances() != null) {
			            for (ProductInstance productInstance : subscription.getProductInstances()) {
			                CustomFieldsDto customFieldsDTO = null;
			                customFieldsDTO = getCustomFieldsDTO(inheritCF, productInstancesCfts, productInstance);
			                dto.getProductInstances().add(new ProductInstanceDto(productInstance, customFieldsDTO));
			            }
			        }
				} catch (CustomFieldException e) {
					e.printStackTrace();
				}
			}
			
			res.add(dto);
			
		}
		return res;
	}

	private CustomFieldsDto getCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF, Map<String, CustomFieldTemplate> cfts,
			ICustomFieldEntity entity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		for (Map.Entry<String, CustomFieldTemplate> entry : cfts.entrySet()) {
			CustomFieldTemplate cft = entry.getValue();
			if (entity.getCftAppliesTo().equals(cft.getAppliesTo())) {

				Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues() != null
						? entity.getCfValues().getValuesByCode()
						: new HashMap<>();

				boolean isValueMapEmpty = cfValuesByCode == null || cfValuesByCode.isEmpty();
				boolean mergeMapValues = inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED;
				boolean includeInheritedCF = mergeMapValues || inheritCF == CustomFieldInheritanceEnum.INHERIT_NO_MERGE;

				if (!isValueMapEmpty) {

					for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValuesByCode.entrySet()) {
						String cfCode = cfValueInfo.getKey();
						// Return only those values that have cft
						if (!cfts.containsKey(cfCode)) {
							continue;
						}
						for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
							currentEntityCFs.getCustomField().add(entityToDtoConverter.customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
						}
					}
				}

				// add parent cf values if inherited
				if (includeInheritedCF) {
					ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
					if (parentEntities != null) {

						Set<String> peDistinctAtvs = new HashSet<String>();
						for (ICustomFieldEntity pentity : parentEntities) {
							String peAppliesToValue = CustomFieldTemplateService.calculateAppliesToValue(entity);
							pentity.setCftAppliesTo(peAppliesToValue);
							peDistinctAtvs.add(peAppliesToValue);
						}
						
						Map<String, CustomFieldTemplate> pCfts = customFieldTemplateService.findByAppliesTo(peDistinctAtvs);

						for (ICustomFieldEntity parentEntity : parentEntities) {
							if (parentEntity instanceof Provider && ((Provider) parentEntity).getCode() == null) {
								parentEntity = appProvider;
							}

							CustomFieldsDto parentCFs = getCustomFieldsDTO(inheritCF, pCfts, parentEntity);
							if (parentCFs != null) {

								for (CustomFieldDto parentCF : parentCFs.getCustomField()) {
									CustomFieldTemplate template = cfts.get(parentCF.getCode());
									if (template != null) {
										currentEntityCFs.getInheritedCustomField().add(parentCF);
									}
								}

								mergeMapValues(parentCFs.getInheritedCustomField(),
										currentEntityCFs.getInheritedCustomField());

								if (mergeMapValues) {
									mergeMapValues(parentCFs.getCustomField(), currentEntityCFs.getCustomField());
									mergeMapValues(parentCFs.getInheritedCustomField(),
											currentEntityCFs.getCustomField());
								}
							}
						}
					}
				}
				break;
			}
		}

		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
	}

	private void mergeMapValues(List<CustomFieldDto> source, List<CustomFieldDto> destination) {
		for (CustomFieldDto sourceCF : source) {
			// logger.trace("Source custom field: {}", sourceCF);
			boolean found = false;
			// look for a matching CF in the destination
			for (CustomFieldDto destinationCF : destination) {
				// logger.trace("Comparing to destination custom field: {}", destinationCF);
				found = destinationCF.getCode().equalsIgnoreCase(sourceCF.getCode());
				if (found) {
					// logger.trace("Custom field matched: \n{}\n{}", sourceCF, destinationCF);
					Map<String, CustomFieldValueDto> sourceValues = sourceCF.getMapValue();
					if (sourceValues != null) {
						Map<String, CustomFieldValueDto> destinationValues = destinationCF.getMapValue();
						for (Entry<String, CustomFieldValueDto> sourceValue : sourceValues.entrySet()) {
							CustomFieldValueDto destinationValue = destinationValues.get(sourceValue.getKey());
							// the source value is not allowed to override the destination value, so only
							// add
							// the values that are on the source CF, but not on the destination CF
							if (destinationValue == null) {
								destinationValues.put(sourceValue.getKey(), sourceValue.getValue());
							}
						}
					}
					break;
				}
			}
			// after comparing all CFs, add the source CF that doesn't exist yet in the
			// destination
			if (!found) {
				destination.add(sourceCF);
			}
		}
	}

	/**
	 * Convert subscription dto to entity
	 * 
	 * @param subscription instance of Subscription to be mapped
	 * @return instance of SubscriptionDto
	 */
	public SubscriptionDto subscriptionToDto(Subscription subscription) {

		SubscriptionDto dto = new SubscriptionDto();
		dto.setCode(subscription.getCode());
		dto.setDescription(subscription.getDescription());
		dto.setStatus(subscription.getStatus());
		dto.setStatusDate(subscription.getStatusDate());
		dto.setOrderNumber(subscription.getOrderNumber());

		if (subscription.getUserAccount() != null) {
			dto.setUserAccount(subscription.getUserAccount().getCode());
		}

		if (subscription.getOffer() != null) {
			dto.setOfferTemplate(subscription.getOffer().getCode());
		}

		dto.setSubscriptionDate(subscription.getSubscriptionDate());
		dto.setTerminationDate(subscription.getTerminationDate());
		if (subscription.getSubscriptionTerminationReason() != null) {
			dto.setTerminationReason(subscription.getSubscriptionTerminationReason().getCode());
		}
		dto.setEndAgreementDate(subscription.getEndAgreementDate());

		dto.setSubscribedTillDate(subscription.getSubscribedTillDate());
		dto.setRenewed(subscription.isRenewed());
		dto.setRenewalNotifiedDate(subscription.getRenewalNotifiedDate());
		dto.setRenewalRule(new SubscriptionRenewalDto(subscription.getSubscriptionRenewal()));

		return dto;
	}

}