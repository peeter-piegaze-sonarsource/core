package org.meveo.admin.job.cluster.message.handler;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSConsumer;
import javax.jms.Message;
import javax.jms.Queue;

import org.meveo.admin.job.cluster.ClusterJobTopicDto;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RecurringRatingJobMessageHandler extends BaseJobMessageHandler {

	@Resource(lookup = "java:/queue/RATEDTRANSACTIONSJOBQUEUE")
	private Queue queue;

	public void processMessage(ClusterJobTopicDto topicMessage) {
		JMSConsumer consumer = context.createConsumer(queue);
		Message message = consumer.receive();

		log.debug("Queue message received {}", message);
	}

}
