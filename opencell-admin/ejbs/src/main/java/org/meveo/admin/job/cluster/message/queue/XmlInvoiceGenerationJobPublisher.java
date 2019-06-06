package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.meveo.admin.job.XMLInvoiceGenerationJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion version
 */
/**
 * Publish a subset of id to be process for {@link XMLInvoiceGenerationJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/XMLINVOICEGENERATIONJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "XmlInvoiceGenerationJobQueue") })
@Stateless
public class XmlInvoiceGenerationJobPublisher extends BaseJobQueuePublisher {

	@Resource(lookup = "java:/queue/XMLINVOICEGENERATIONJOBQUEUE")
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
