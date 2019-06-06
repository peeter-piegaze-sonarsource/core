package org.meveo.admin.job.cluster.message.queue.consumer;

import java.util.Date;
import java.util.stream.Collectors;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.meveo.admin.async.RecurringChargeAsync;
import org.meveo.admin.job.RecurringRatingJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.RecurringRatingJobPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;

/**
 * Handles cluster message for {@link RecurringRatingJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@MessageDriven(name = "RecurringRatingJobQueueConsumer", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/RECURRINGRATINGJOBQUEUE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class RecurringRatingJobQueueConsumer extends BaseJobQueueConsumer implements MessageListener {

	@Inject
	private RecurringRatingJobPublisher recurringRatingJobPublisher;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private RecurringChargeAsync recurringChargeAsync;

	@Override
	public void onMessage(Message msg) {
		log.debug("----------------------------------");
		log.debug("Received message in queue {}", msg);

		ClusterJobQueueDto message = deserialized(msg);
		if (message != null) {
			processMessage(message);
		}
	}

	private void processMessage(ClusterJobQueueDto message) {
		try {
			MeveoUser lastCurrentUser = initUser(message.getProviderCode(), message.getCurrentUserName());
			JobExecutionResultImpl result = new JobExecutionResultImpl();
			if (message.getJobExecutionResultImplId() != null) {
				result = jobExecutionService.findById(message.getJobExecutionResultImplId());
			}

			Date rateUntilDate = new Date();
			Object rateUntilDateObj = message.getParameter(ClusterJobQueueDto.RATE_UNTIL_DATE);
			if (rateUntilDateObj != null) {
				rateUntilDate = DateUtils.guessDate(rateUntilDateObj.toString(), DateUtils.SIMPLE_DATE_FORMAT);
			}

			if (message.getItems() != null) {
				recurringChargeAsync.launchAndForget(
						message.getItems().stream().map(e -> (Long) e).collect(Collectors.toList()), result,
						rateUntilDate, lastCurrentUser);
			}

		} catch (Exception e) {
			log.error("Failed processing job cluster message {}", e.getMessage());
			super.requeue(message);
		}
	}

	@Override
	protected void republish(ClusterJobQueueDto message) {
		recurringRatingJobPublisher.publishMessage(message);
	}
}
