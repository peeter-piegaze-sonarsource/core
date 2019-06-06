package org.meveo.admin.job.cluster.message.queue.consumer;

import java.util.stream.Collectors;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.admin.job.ReportExtractJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.UsageRatingJobPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;

/**
 * Handles cluster message for {@link ReportExtractJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@MessageDriven(name = "UsageRatingJobQueueConsumer", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/USAGERATINGJOBQUEUE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class UsageRatingJobQueueConsumer extends BaseJobQueueConsumer implements MessageListener {

	@Inject
	private UsageRatingJobPublisher usageRatingJobPublisher;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private UsageRatingAsync usageRatingAsync;

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

			if (message.getItems() != null) {
				usageRatingAsync.launchAndForget(
						message.getItems().stream().map(e -> (Long) e).collect(Collectors.toList()), result,
						lastCurrentUser);
			}

		} catch (Exception e) {
			super.requeue(message);
		}
	}

	@Override
	protected void republish(ClusterJobQueueDto message) {
		usageRatingJobPublisher.publishMessage(message);
	}

}
