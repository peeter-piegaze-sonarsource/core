package org.meveo.admin.job;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class ReRatingJobBean extends BaseJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226065462536318643L;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatingService ratingService;

	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<WalletOperation> rejectededOperationProducer;
	
    @Inject
    private JobExecutionService jobExecutionService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, boolean useSamePricePlan) {
		log.debug("Running useSamePricePlan={}", useSamePricePlan);
		
		try {
			List<Long> walletOperationIds = walletOperationService.listToRerate();

			log.info("rerate with useSamePricePlan={} ,#operations={}", useSamePricePlan,walletOperationIds.size());
			result.setNbItemsToProcess(walletOperationIds.size());
			int i = 0;
			for (Long walletOperationId : walletOperationIds) {
			    i++;
	            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
	                break;
	            }
				try {
					ratingService.reRate(walletOperationId, useSamePricePlan);
					result.registerSucces();
				} catch (Exception e) {
					//rejectededOperationProducer.fire(walletOperationId);
				    log.error("Failed to rerate operation {}", walletOperationId, e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error("Failed to rerate operations", e);
		}
	}
}
