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
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.ReportExtractAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.ReportExtractJobPublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.finance.ReportExtractService;

/**
 * List all ReportExtract and dispatched for asynch execution.
 * 
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class ReportExtractJobBean extends BaseJobBean implements Serializable {

	private static final long serialVersionUID = 9159856207913605563L;

	@Inject
	private ReportExtractService reportExtractService;

	@Inject
	private ReportExtractAsync reportExtractAsync;

	@Inject
	private ReportExtractJobPublisher reportExtractJobPublisher;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("start in running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}

		try {
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = (Date) this.getParamOrCFValue(jobInstance, "startDate");
				endDate = (Date) this.getParamOrCFValue(jobInstance, "endDate");

			} catch (Exception e) {
				log.warn("Cant get customFields for {} {}", jobInstance.getJobTemplate(), e.getMessage());
			}

			List<Long> reportExtractIds = reportExtractService.listIds();
			log.debug("Report to execute={}", (reportExtractIds == null ? null : reportExtractIds.size()));

			SubListCreator subListCreator = new SubListCreator(reportExtractIds, nbRuns.intValue());

			if (EjbUtils.isRunningInClusterMode()) {
				executeJobInCluster(result, subListCreator, startDate, endDate);

			} else {
				executeJobInNode(result, jobInstance, subListCreator, startDate, endDate);
			}

		} catch (Exception e) {
			log.error("Failed to run {} job {}", getClass().getSimpleName(), e.getMessage());
			result.registerError(e.getMessage());
		}
		log.debug("end running {}!", getClass().getSimpleName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeJobInCluster(JobExecutionResultImpl result, SubListCreator subListCreator, Date startDate,
			Date endDate) {
		jobExecutionService.persistResult(ReportExtractJob.class.getName(), result, result.getJobInstance());
		while (subListCreator.isHasNext()) {
			ClusterJobQueueDto queueDto = initClusterQueueDto(result, new ArrayList<>(subListCreator.getNextWorkSet()),
					subListCreator.getNbThreads());
			queueDto.addParameter(ClusterJobQueueDto.START_DATE, startDate);
			queueDto.addParameter(ClusterJobQueueDto.END_DATE, endDate);

			// send to queue
			reportExtractJobPublisher.publishMessage(queueDto);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeJobInNode(JobExecutionResultImpl result, JobInstance jobInstance, SubListCreator subListCreator,
			Date startDate, Date endDate) {
		try {
			List<Future<String>> futures = new ArrayList<>();
			MeveoUser lastCurrentUser = currentUser.unProxy();

			Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
			if (nbRuns == -1) {
				nbRuns = (long) Runtime.getRuntime().availableProcessors();
			}
			Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

			log.debug("block to run: {}", subListCreator.getBlocToRun());
			log.debug("nbThreads: {}", nbRuns);

			while (subListCreator.isHasNext()) {
				futures.add(reportExtractAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result,
						startDate, endDate, lastCurrentUser));
				if (subListCreator.isHasNext()) {
					try {
						Thread.sleep(waitingMillis.longValue());

					} catch (InterruptedException e) {
						log.error("", e);
						Thread.currentThread().interrupt();

					}
				}
			}
			// Wait for all async methods to finish
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
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute1(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("start in running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}
		Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

		try {
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = (Date) this.getParamOrCFValue(jobInstance, "startDate");
				endDate = (Date) this.getParamOrCFValue(jobInstance, "endDate");

			} catch (Exception e) {
				log.warn("Cant get customFields for {} {}", jobInstance.getJobTemplate(), e.getMessage());
			}

			List<Long> reportExtractIds = reportExtractService.listIds();
			log.debug("Report to execute={}", (reportExtractIds == null ? null : reportExtractIds.size()));

			List<Future<String>> futures = new ArrayList<>();
			SubListCreator subListCreator = new SubListCreator(reportExtractIds, nbRuns.intValue());

			log.debug("block to run: {}", subListCreator.getBlocToRun());
			log.debug("nbThreads: {}", nbRuns);
			MeveoUser lastCurrentUser = currentUser.unProxy();
			while (subListCreator.isHasNext()) {
				futures.add(reportExtractAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result,
						startDate, endDate, lastCurrentUser));
				if (subListCreator.isHasNext()) {
					try {
						Thread.sleep(waitingMillis.longValue());

					} catch (InterruptedException e) {
						log.error("", e);
						Thread.currentThread().interrupt();

					}
				}
			}
			// Wait for all async methods to finish
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
		log.debug("end running {}!", getClass().getSimpleName());
	}

}
