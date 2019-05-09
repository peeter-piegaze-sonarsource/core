package org.meveo.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.mediation.Access;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to calculate applies to values
 * 
 * @author melyoussoufi
 * @lastModifiedVersion 5.0.7
 *
 */
public class AppliesToValuesCalculator {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	Set<String> allAtvs;

	public AppliesToValuesCalculator() {
		allAtvs = new HashSet<String>();
	}

	public void calculateOfferTemplatesAtvs(List<OfferTemplate> offerTemplates) {

		allAtvs = new HashSet<String>();

		for (OfferTemplate ot : offerTemplates) {
			try {
				addAppliesToValue(ot);
			} catch (CustomFieldException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}

	}

	public void calculateSubscriptionsAtvs(List<Subscription> subscriptions) {

		String subscriptionAptv = null;
		String accessAptv = null;
		String siAptv = null;
		String pitv = null;

		try {
			subscriptionAptv = CustomFieldTemplateService.calculateAppliesToValue(Subscription.class);
			accessAptv = CustomFieldTemplateService.calculateAppliesToValue(Access.class);
			siAptv = CustomFieldTemplateService.calculateAppliesToValue(ServiceInstance.class);
			pitv = CustomFieldTemplateService.calculateAppliesToValue(ProductInstance.class);
		} catch (CustomFieldException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		allAtvs = new HashSet<String>();
		allAtvs.add(subscriptionAptv);
		allAtvs.add(accessAptv);
		allAtvs.add(siAptv);
		allAtvs.add(pitv);

		for (Subscription subscription : subscriptions) {
			subscription.setCftAppliesTo(subscriptionAptv);
			List<Access> accessPoints = subscription.getAccessPoints();
			if (accessPoints != null) {
				for (Access ap : accessPoints) {
					ap.setCftAppliesTo(accessAptv);
				}
			}
			List<ServiceInstance> servicesInstances = subscription.getServiceInstances();
			if (servicesInstances != null) {
				for (ServiceInstance si : servicesInstances) {
					si.setCftAppliesTo(siAptv);
				}
			}
			List<ProductInstance> productsInstances = subscription.getProductInstances();
			if (productsInstances != null) {
				for (ProductInstance pi : productsInstances) {
					pi.setCftAppliesTo(pitv);

				}
			}
		}
	}

	public void calculateCustomEntityInstancesAtvs(List<CustomEntityInstance> customEntityInstances) {

		allAtvs = new HashSet<String>();

		for (CustomEntityInstance cei : customEntityInstances) {
			try {
				addAppliesToValueForCei(cei);
			} catch (CustomFieldException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}

	}

	private void addAppliesToValueForCei(CustomEntityInstance cei) throws CustomFieldException {
		String appliesToValue = CustomFieldTemplateService.calculateAppliesToValue(cei);
		if (appliesToValue != null) {
			cei.setCftAppliesTo(appliesToValue);
			allAtvs.add(appliesToValue);
		} else {
			cei.setCftAppliesTo(null);
		}
	}

	private void addAppliesToValue(BusinessCFEntity businessCFEntity) throws CustomFieldException {
		String appliesToValue = CustomFieldTemplateService.calculateAppliesToValue(businessCFEntity.getClass());
		if (appliesToValue != null) {
			businessCFEntity.setCftAppliesTo(appliesToValue);
			allAtvs.add(appliesToValue);
		} else {
			businessCFEntity.setCftAppliesTo(null);
		}
	}

	public Set<String> getAllAtvs() {
		return allAtvs;
	}

}
