package org.meveo.admin.job.cluster.message.handler;

import javax.inject.Inject;
import javax.jms.JMSContext;

import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class BaseJobMessageHandler {

	@Inject
	protected Logger log;

	@Inject
	protected JMSContext context;

}
