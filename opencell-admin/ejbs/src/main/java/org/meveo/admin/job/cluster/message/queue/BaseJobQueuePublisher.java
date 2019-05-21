package org.meveo.admin.job.cluster.message.queue;

import javax.inject.Inject;
import javax.jms.JMSContext;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class BaseJobQueuePublisher {

	@Inject
	protected Logger log;

	@Inject
	protected JMSContext context;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

}
