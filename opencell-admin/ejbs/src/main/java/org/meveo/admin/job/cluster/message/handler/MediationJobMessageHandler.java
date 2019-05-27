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

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class MediationJobMessageHandler extends BaseJobMessageHandler {

	@Resource(lookup = "java:/queue/MEDIATIONJOBQUEUE")
	private Queue queue;

	public void processMessage(ClusterJobTopicDto topicMessage) {
		log.debug("Receive a topic message {}", topicMessage);

		try (JMSConsumer consumer = context.createConsumer(queue)) {
			Message msg = consumer.receive();

			try {
				if (msg instanceof ObjectMessage) {
					ClusterJobQueueDto clusterJobQueueDto;
					clusterJobQueueDto = msg.getBody(ClusterJobQueueDto.class);
					log.debug("Queue message received {}", clusterJobQueueDto);
					msg.acknowledge();
				}

			} catch (JMSException e) {
				log.error("Failed reading cluster job message {}", e.getMessage());
			}
		}
	}
}