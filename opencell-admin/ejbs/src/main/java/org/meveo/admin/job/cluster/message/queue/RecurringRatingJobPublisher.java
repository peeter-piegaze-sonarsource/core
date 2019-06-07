package org.meveo.admin.job.cluster.message.queue;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;

import org.meveo.admin.job.UsageRatingJob;

/**
 * Publish a subset of id to be process for {@link UsageRatingJob}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/RECURRINGRATINGJOBQUEUE", interfaceName = "javax.jms.Queue", destinationName = "RecurringRatingJobQueue") })
@Stateless
public class RecurringRatingJobPublisher extends BaseJobQueuePublisher {

	@Resource(lookup = "java:/queue/RECURRINGRATINGJOBQUEUE")
	private Queue queue;

	@Override
	protected Queue getQueue() {
		return queue;
	}
}
