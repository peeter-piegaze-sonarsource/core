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
		@JMSDestinationDefinition(name = "java:/queue/RATEDTRANSACTIONSJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "RatedTransactionsJobQueue") })
@Stateless
public class RatedTransactionsJobQueuePublisher {

	@Inject
	private Logger log;

	@Inject
	private JMSContext context;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Resource(lookup = "java:/queue/RATEDTRANSACTIONSJOBQUEUE")
	private Queue queue;

	public void publishMessage(ClusterJobQueueDto messageDto) {

		if (!EjbUtils.isRunningInClusterMode()) {
			return;
		}

		try {
			messageDto.setSourceNode(EjbUtils.getCurrentClusterNode());
			log.trace("Publishing subset of ids between cluster nodes event {}", messageDto);
			
			context.createProducer().send(queue, messageDto);

		} catch (Exception e) {
			log.error("Failed to publish subset of ids between cluster nodes event", e);
		}
	}

}
