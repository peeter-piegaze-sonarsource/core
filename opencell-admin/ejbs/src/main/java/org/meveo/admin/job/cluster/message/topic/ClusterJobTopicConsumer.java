package org.meveo.admin.job.cluster.message.topic;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.meveo.admin.job.MediationJob;
import org.meveo.admin.job.RatedTransactionsJob;
import org.meveo.admin.job.RecurringRatingJob;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.admin.job.cluster.message.handler.MediationJobMessageHandler;
import org.meveo.admin.job.cluster.message.handler.RecurringRatingJobMessageHandler;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@MessageDriven(name = "ClusterJobTopicReceiver", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/CLUSTERJOBTOPIC"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ClusterJobTopicConsumer implements MessageListener {

	@Inject
	private Logger log;

	@Inject
	private RecurringRatingJobMessageHandler recurringRatingJobMessageHandler;

	@Inject
	private MediationJobMessageHandler mediationJobMessageHandler;

	@Override
	public void onMessage(Message message) {

		try {
			if (message instanceof ObjectMessage) {
				ClusterJobTopicDto messageDto = (ClusterJobTopicDto) ((ObjectMessage) message).getObject();
				// if (EjbUtils.getCurrentClusterNode().equals(messageDto.getSourceNode())) {
				// return;
				// }
				log.info("Received cluster job execution {}", message);

				processClusterMessage(messageDto);

			} else {
				log.warn("Unhandled cluster job execution event {}", message.getClass().getName());
			}
		} catch (Exception e) {
			log.error("Failed to process JMS message {}", e.getMessage());
		}
	}

	private void processClusterMessage(ClusterJobTopicDto message) {
		log.debug("Reading data from {}_Job queue with data={}", message.getJobTemplate(), message);
		
		if (message.getJobTemplate().equals(RatedTransactionsJob.class.getSimpleName())) {

		} else if (message.getJobTemplate().equals(RecurringRatingJob.class.getSimpleName())) {
			recurringRatingJobMessageHandler.processMessage(message);

		} else if (message.getJobTemplate().equals(MediationJob.class.getSimpleName())) {
			mediationJobMessageHandler.processMessage(message);
		}
	}

}
