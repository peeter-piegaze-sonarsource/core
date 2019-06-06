package org.meveo.admin.job.cluster.message.queue.consumer;

import java.util.Date;
import java.util.stream.Collectors;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.meveo.admin.async.ReportExtractAsync;
import org.meveo.admin.job.ReportExtractJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.ReportExtractJobPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;

/**
 * Handles cluster message for {@link ReportExtractJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@MessageDriven(name = "ReportExtractJobQueueConsumer", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/REPORTEXTRACTJOBQUEUE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ReportExtractJobQueueConsumer extends BaseJobQueueConsumer implements MessageListener {

	@Inject
	private ReportExtractJobPublisher reportExtractJobPublisher;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private ReportExtractAsync reportExtractAsync;

	@Override
	public void onMessage(Message msg) {
		super.onMessageReceive(msg);
	}

	@Override
	protected void processMessage(ClusterJobQueueDto message) {
		try {
			MeveoUser lastCurrentUser = initUser(message.getProviderCode(), message.getCurrentUserName());
			JobExecutionResultImpl result = new JobExecutionResultImpl();
			if (message.getJobExecutionResultImplId() != null) {
				result = jobExecutionService.findById(message.getJobExecutionResultImplId());
			}

			Object startDateObj = message.getParameter(ClusterJobQueueDto.START_DATE);
			Object endDateObj = message.getParameter(ClusterJobQueueDto.END_DATE);

			Date startDate = null;
			Date endDate = null;

			if (startDateObj != null) {
				startDate = DateUtils.guessDate(startDateObj.toString(), DateUtils.SIMPLE_DATE_FORMAT);
			}
			if (endDateObj != null) {
				endDate = DateUtils.guessDate(endDateObj.toString(), DateUtils.SIMPLE_DATE_FORMAT);
			}

			if (message.getItems() != null) {
				reportExtractAsync.launchAndForget(
						message.getItems().stream().map(e -> (Long) e).collect(Collectors.toList()), result, startDate,
						endDate, lastCurrentUser);
			}

		} catch (Exception e) {
			super.requeue(message);
		}
	}

	@Override
	protected void republish(ClusterJobQueueDto message) {
		reportExtractJobPublisher.publishMessage(message);
	}

}
