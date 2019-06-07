package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;

import org.meveo.admin.job.PDFInvoiceGenerationJob;

/**
 * Publish a subset of id to be process for {@link PDFInvoiceGenerationJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/PDFINVOICEGENERATIONJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "PdfInvoiceGenerationJobQueue") })
@Stateless
public class PdfInvoiceGenerationJobPublisher extends BaseJobQueuePublisher {

	@Resource(lookup = "java:/queue/PDFINVOICEGENERATIONJOBQUEUE")
	private Queue queue;

	@Override
	protected Queue getQueue() {
		return queue;
	}
}
