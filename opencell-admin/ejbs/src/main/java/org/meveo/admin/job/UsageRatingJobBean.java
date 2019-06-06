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
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.UsageRatingJobPublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;

/**
 * Job bean for UsageRatingJob.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UsageRatingJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private EdrService edrService;

	@Inject
	private UsageRatingAsync usageRatingAsync;

	@Inject
	@Rejected
	private Event<Serializable> rejectededEdrProducer;

	@Inject
	private UsageRatingJobPublisher usageRatingJobPublisher;

	/**
	 * Number of EDRS to process in a single job run
	 */
	private static int PROCESS_NR_IN_JOB_RUN = 2000000;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}

		try {
			Date rateUntilDate = null;
			String ratingGroup = null;
			try {
				rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
				ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");

			} catch (Exception e) {
				log.warn("Cant get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
			}

			int maxRecordToProcess = EjbUtils.isRunningInClusterMode() ? 0 : PROCESS_NR_IN_JOB_RUN;

			List<Long> ids = edrService.getEDRidsToRate(rateUntilDate, ratingGroup, maxRecordToProcess);
			result.setNbItemsToProcess(ids.size());

			SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());

			log.debug("Execute {} size={}, block to run={}, nbThreads={}", getClass().getSimpleName(),
					(ids == null ? null : ids.size()), subListCreator.getBlocToRun(), nbRuns);

			if (EjbUtils.isRunningInClusterMode()) {
				executeJobInCluster(result, subListCreator, rateUntilDate, ratingGroup);

			} else {
				executeJobInNode(result, subListCreator, rateUntilDate, ratingGroup);
			}

		} catch (Exception e) {
			log.error("Failed to run usage rating job", e);
			result.registerError(e.getMessage());
			result.addReport(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeJobInNode(JobExecutionResultImpl result, SubListCreator subListCreator, Date rateUntilDate,
			String ratingGroup) {
		try {
			List<Future<String>> futures = new ArrayList<>();
			MeveoUser lastCurrentUser = currentUser.unProxy();

			Long waitingMillis = (Long) this.getParamOrCFValue(result.getJobInstance(), "waitingMillis", 0L);

			while (subListCreator.isHasNext()) {
				futures.add(usageRatingAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result,
						lastCurrentUser));
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
					result.addReport(cause.getMessage());
					log.error("Failed to execute async method", cause);
				}
			}

			List<Long> ids = edrService.getEDRidsToRate(rateUntilDate, ratingGroup, 1);
			result.setDone(ids.isEmpty());

		} catch (Exception e) {
			log.error("Failed to run {} job {}", getClass().getSimpleName(), e);
			result.registerError(e.getMessage());
		}

		log.debug("end running {}", getClass().getSimpleName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeJobInCluster(JobExecutionResultImpl result, SubListCreator subListCreator, Date rateUntilDate,
			String ratingGroup) {
		jobExecutionService.persistResult(UsageRatingJob.class.getName(), result, result.getJobInstance());
		while (subListCreator.isHasNext()) {
			ClusterJobQueueDto queueDto = initClusterQueueDto(result, new ArrayList<>(subListCreator.getNextWorkSet()),
					subListCreator.getNbThreads());

			// send to queue
			usageRatingJobPublisher.publishMessage(queueDto);
		}
	}

}
