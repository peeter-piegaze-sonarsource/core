package org.meveo.admin.job.cluster.message.handler;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.commons.utils.EjbUtils;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class MediationJobMessageHandler extends BaseJobMessageHandler {

	@Resource(lookup = "java:/queue/MEDIATIONJOBQUEUE")
	private Queue queue;

	public void processMessage(ClusterJobTopicDto topicMessage) {
		try (JMSConsumer consumer = context.createConsumer(queue)) {
			Message msg = consumer.receive();

			try {
				if (msg instanceof ObjectMessage) {
					ClusterJobQueueDto clusterJobQueueDto;
					clusterJobQueueDto = msg.getBody(ClusterJobQueueDto.class);
					msg.acknowledge();

					log.debug("Queue message received in node={} => {}", EjbUtils.getCurrentClusterNode(),
							clusterJobQueueDto);
				}

			} catch (JMSException e) {
				log.error("Failed reading cluster job message {}", e.getMessage());
			}
		}
	}
}