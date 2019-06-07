package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;

import org.meveo.admin.job.XMLInvoiceGenerationJob;

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

	@Override
	protected Queue getQueue() {
		return queue;
	}
}
