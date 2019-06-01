package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.meveo.admin.job.ReportExtractJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;

/**
 * Cluster message publisher for {@link ReportExtractJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/REPORTEXTRACTJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "ReportExtractJobQueue") })
@Stateless
public class ReportExtractJobPublisher extends BaseJobQueuePublisher {

	@Resource(lookup = "java:/queue/REPORTEXTRACTJOBQUEUE")
	private Queue queue;

	public void publishMessage(ClusterJobQueueDto messageDto) {
		try {
			log.trace("Publishing subset of ids between cluster nodes event {}", messageDto);
			ObjectMessage message = context.createObjectMessage();
			message.setObject(messageDto);
			context.createProducer().send(queue, message);

		} catch (Exception e) {
			log.error("Failed to publish subset of ids between cluster nodes event", e);
		}
	}
}
