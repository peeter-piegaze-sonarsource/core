package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;

import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/RECURRINGRATINGJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "RecurringRatingJobQueue") })
@Stateless
public class RecurringRatingJobQueuePublisher {

	@Inject
	private Logger log;

	@Inject
	private JMSContext context;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Resource(lookup = "java:/queue/RECURRINGRATINGJOBQUEUE")
	private Queue queue;

	public void publishMessage(ClusterJobQueueDto messageDto) {

		if (!EjbUtils.isRunningInClusterMode()) {
			return;
		}

		try {
			log.trace("Publishing subset of ids between cluster nodes {}", messageDto);

			context.createProducer().send(queue, messageDto);

		} catch (Exception e) {
			log.error("Failed to publish subset of ids between cluster nodes {}", e.getMessage());
		}
	}
}
