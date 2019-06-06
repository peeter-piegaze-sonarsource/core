package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.async.RecurringChargeAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.RecurringRatingJobPublisher;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;

/**
 * Job bean for {@link RecurringRatingJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RecurringRatingJobBean extends BaseJobBean implements Serializable {

	private static final long serialVersionUID = 2226065462536318643L;

	@Inject
	private RecurringChargeAsync recurringChargeAsync;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	@Rejected
	private Event<Serializable> rejectededChargeProducer;

	@Inject
	private RecurringRatingJobPublisher recurringRatingJobPublisher;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	// @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("start in running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}

		try {
			Date rateUntilDate = null;
			try {
				rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");

			} catch (Exception e) {
				log.warn("Cant get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
			}
			if (rateUntilDate == null) {
				rateUntilDate = new Date();
			}

			List<Long> ids = recurringChargeInstanceService.findIdsByStatus(InstanceStatusEnum.ACTIVE, rateUntilDate,
					false);
			result.setNbItemsToProcess(ids.size());

			SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());

			log.debug("Execute {} size={}, block to run={}, nbThreads={}", getClass().getSimpleName(),
					(ids == null ? null : ids.size()), subListCreator.getBlocToRun(), nbRuns);

			if (EjbUtils.isRunningInClusterMode()) {
				executeJobInCluster(result, subListCreator, rateUntilDate);

			} else {
				executeJobInNode(result, subListCreator, rateUntilDate);
			}

		} catch (Exception e) {
			log.error("Failed to run recurring rating job", e);
			result.registerError(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeJobInNode(JobExecutionResultImpl result, SubListCreator subListCreator, Date rateUntilDate) {
		try {
			List<Future<String>> futures = new ArrayList<>();
			MeveoUser lastCurrentUser = currentUser.unProxy();

			Long waitingMillis = (Long) this.getParamOrCFValue(result.getJobInstance(), "waitingMillis", 0L);

			while (subListCreator.isHasNext()) {
				futures.add(recurringChargeAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result,
						rateUntilDate, lastCurrentUser));
				if (subListCreator.isHasNext()) {
					try {
						Thread.sleep(waitingMillis.longValue());

					} catch (InterruptedException e) {
						log.error("", e);
						Thread.currentThread().interrupt();
					}
				}
			}

			for (Future<String> future : futures) {
				try {
					future.get();

				} catch (InterruptedException e) {
					// It was cancelled from outside - no interest
					Thread.currentThread().interrupt();

				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					result.registerError(cause.getMessage());
					log.error("Failed to execute async method", cause);
				}
			}

		} catch (Exception e) {
			log.error("Failed to run {} job {}", getClass().getSimpleName(), e);
			result.registerError(e.getMessage());
		}

		log.debug("end running {}", getClass().getSimpleName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeJobInCluster(JobExecutionResultImpl result, SubListCreator subListCreator, Date rateUntilDate) {
		jobExecutionService.persistResult(ReportExtractJob.class.getName(), result, result.getJobInstance());
		while (subListCreator.isHasNext()) {
			ClusterJobQueueDto queueDto = initClusterQueueDto(result, new ArrayList<>(subListCreator.getNextWorkSet()),
					subListCreator.getNbThreads());
			queueDto.addParameter(ClusterJobQueueDto.RATE_UNTIL_DATE, rateUntilDate);

			// send to queue
			recurringRatingJobPublisher.publishMessage(queueDto);
		}
	}
}
