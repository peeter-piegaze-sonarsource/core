package org.meveo.admin.job.cluster.message.queue;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * Base class for cluster job publishers.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class BaseJobQueuePublisher {

	@Inject
	protected Logger log;

	@Inject
	protected JMSContext context;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	protected abstract Queue getQueue();

	public void publishMessage(ClusterJobQueueDto messageDto) {
		try {
			log.trace("Publishing subset of ids between cluster nodes event {}", messageDto);
			ObjectMessage message = context.createObjectMessage();
			message.setObject(messageDto);
			context.createProducer().send(getQueue(), message);

		} catch (Exception e) {
			log.error("Failed to publish subset of ids between cluster nodes event", e);
		}
	}

}
