package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RatingStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;

/**
 * Unit bean for managing {@link RecurringRatingJob}.
 * 
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UnitRecurringRatingJobBean implements Serializable {

	private static final long serialVersionUID = 2226065462536318643L;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	protected Logger log;

	@JpaAmpNewTx
	// @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, Long recurringChargeId, Date maxDate) {
		log.debug("--------------------Processing activeRecurringChargeInstanceID {}", recurringChargeId);
		
		try {
			RatingStatus ratingStatus = recurringChargeInstanceService.applyRecurringCharge(recurringChargeId, maxDate);
			if (ratingStatus.getNbRating() == 1) {
				result.registerSucces();

			} else if (ratingStatus.getNbRating() > 1) {
				result.registerWarning(recurringChargeId + " rated " + ratingStatus.getNbRating() + " times");

			} else {
				if (ratingStatus.getStatus() != RatingStatusEnum.NOT_RATED_FALSE_FILTER) {
					result.registerWarning(recurringChargeId + " not rated");
				}
			}

		} catch (BusinessException e) {
			result.registerError(recurringChargeId, e.getMessage());
		}
	}
}