package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.PdfInvoiceGenerationJobPublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;

/**
 * Job Bean for handling {@link PDFInvoiceGenerationJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class PDFInvoiceGenerationJobBean extends BaseJobBean {

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private InvoicingAsync invoicingAsync;

	@Inject
	private PdfInvoiceGenerationJobPublisher pdfInvoiceGenerationJobPublisher;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}

		try {
			InvoicesToProcessEnum invoicesToProcessEnum = InvoicesToProcessEnum
					.valueOf((String) this.getParamOrCFValue(jobInstance, "invoicesToProcess", "FinalOnly"));
			String parameter = jobInstance.getParametres();

			Long billingRunId = null;
			if (parameter != null && parameter.trim().length() > 0) {
				try {
					billingRunId = Long.parseLong(parameter);
				} catch (Exception e) {
					log.error("error while getting billing run", e);
					result.registerError(e.getMessage());
				}
			}

			List<Long> ids = this.fetchInvoiceIdsToProcess(invoicesToProcessEnum, billingRunId);

			result.setNbItemsToProcess(ids.size());

			SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());

			log.debug("Execute {} size={}, block to run={}, nbThreads={}", getClass().getSimpleName(),
					(ids == null ? null : ids.size()), subListCreator.getBlocToRun(), nbRuns);

			if (EjbUtils.isRunningInClusterMode()) {
				executeJobInCluster(result, subListCreator);

			} else {
				executeJobInNode(result, subListCreator);
			}

		} catch (Exception e) {
			log.error("Failed to generate PDF invoices", e);
			result.registerError(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeJobInNode(JobExecutionResultImpl result, SubListCreator subListCreator) {
		try {
			List<Future<String>> futures = new ArrayList<Future<String>>();
			MeveoUser lastCurrentUser = currentUser.unProxy();

			Long waitingMillis = (Long) this.getParamOrCFValue(result.getJobInstance(), "waitingMillis", 0L);

			while (subListCreator.isHasNext()) {
				futures.add(invoicingAsync.generatePdfAsync((List<Long>) subListCreator.getNextWorkSet(), result,
						lastCurrentUser));

				if (subListCreator.isHasNext()) {
					try {
						Thread.sleep(waitingMillis.longValue());
					} catch (InterruptedException e) {
						log.error("", e);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeJobInCluster(JobExecutionResultImpl result, SubListCreator subListCreator) {
		jobExecutionService.persistResult(ReportExtractJob.class.getName(), result, result.getJobInstance());
		while (subListCreator.isHasNext()) {
			ClusterJobQueueDto queueDto = initClusterQueueDto(result, new ArrayList<>(subListCreator.getNextWorkSet()),
					subListCreator.getNbThreads());

			// send to queue
			pdfInvoiceGenerationJobPublisher.publishMessage(queueDto);
		}
	}

	private List<Long> fetchInvoiceIdsToProcess(InvoicesToProcessEnum invoicesToProcessEnum, Long billingRunId) {

		log.debug(" fetchInvoiceIdsToProcess for invoicesToProcessEnum = {} and billingRunId = {} ",
				invoicesToProcessEnum, billingRunId);
		List<Long> invoiceIds = null;

		switch (invoicesToProcessEnum) {
		case FinalOnly:
			invoiceIds = invoiceService.getInvoicesIdsValidatedWithNoPdf(billingRunId);
			break;

		case DraftOnly:
			invoiceIds = invoiceService.getDraftInvoiceIdsByBRWithNoPdf(billingRunId);
			break;

		case All:
			invoiceIds = invoiceService.getInvoiceIdsIncludeDraftByBRWithNoPdf(billingRunId);
			break;
		}
		return invoiceIds;

	}
}