package org.meveo.admin.job.cluster.message.queue.consumer;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for cluster job consumers.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class BaseJobQueueConsumer {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Inject
	protected JMSContext context;

	@Resource(lookup = "java:/jms/queue/DLQ")
	private Queue deadLetterQueue;

	protected abstract void republish(ClusterJobQueueDto message);

	protected static MeveoUser initUser(String providerCode, String currentUserName) {
		return MeveoUser.instantiate(currentUserName, providerCode);
	}

	public ClusterJobQueueDto deserialized(Message msg) {
		ClusterJobQueueDto clusterJobQueueDto = null;
		try {
			if (msg instanceof ObjectMessage) {
				clusterJobQueueDto = msg.getBody(ClusterJobQueueDto.class);
				log.debug("Queue message received in node={} => {}", EjbUtils.getCurrentClusterNode(),
						clusterJobQueueDto);

			} else {
				log.debug("No message to read");
			}

		} catch (JMSException e) {
			log.error("Failed reading cluster job message {}", e.getMessage());

		}

		return clusterJobQueueDto;
	}

	public void requeue(ClusterJobQueueDto message) {
		if (message.isValid()) {
			try {
				Thread.sleep(60000);
				message.setDeliveryCount(message.getDeliveryCount() + 1);
				republish(message);

			} catch (InterruptedException ie) {

			}

		} else {
			// write to dead-letter queue
			publicToDeadLetterQueue(message);
		}
	}

	protected void publicToDeadLetterQueue(ClusterJobQueueDto message) {
		try {
			log.debug("Publishing to dead letter queue {}", message);
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(message);
			context.createProducer().send(deadLetterQueue, message);

		} catch (Exception e) {
			log.error("Failed to publish subset of ids between cluster nodes event", e);
		}
	}

}
