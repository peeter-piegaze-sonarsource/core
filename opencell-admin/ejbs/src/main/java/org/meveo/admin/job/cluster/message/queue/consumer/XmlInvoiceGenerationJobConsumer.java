package org.meveo.admin.job.cluster.message.queue.consumer;

import java.util.stream.Collectors;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.job.XMLInvoiceGenerationJob;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.message.queue.XmlInvoiceGenerationJobPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;

/**
 * Handles cluster message for {@link XMLInvoiceGenerationJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@MessageDriven(name = "XmlInvoiceGenerationJobConsumer", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/XMLINVOICEGENERATIONJOBQUEUE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class XmlInvoiceGenerationJobConsumer extends BaseJobQueueConsumer implements MessageListener {

	@Inject
	private XmlInvoiceGenerationJobPublisher xmlInvoiceGenerationJobPublisher;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private InvoicingAsync invoicingAsync;

	@Override
	public void onMessage(Message msg) {
		super.onMessageReceive(msg);
	}

	@Override
	protected void processMessage(ClusterJobQueueDto message) {
		try {
			MeveoUser lastCurrentUser = initUser(message.getProviderCode(), message.getCurrentUserName());
			JobExecutionResultImpl result = new JobExecutionResultImpl();
			if (message.getJobExecutionResultImplId() != null) {
				result = jobExecutionService.findById(message.getJobExecutionResultImplId());
			}

			if (message.getItems() != null) {
				invoicingAsync.generateXmlAsync(
						message.getItems().stream().map(e -> (Long) e).collect(Collectors.toList()), result,
						lastCurrentUser);
			}

		} catch (Exception e) {
			super.requeue(message);
		}
	}

	@Override
	protected void republish(ClusterJobQueueDto message) {
		xmlInvoiceGenerationJobPublisher.publishMessage(message);
	}

}
