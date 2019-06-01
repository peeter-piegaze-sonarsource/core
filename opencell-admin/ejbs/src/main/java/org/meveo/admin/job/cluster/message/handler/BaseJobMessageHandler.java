package org.meveo.admin.job.cluster.message.handler;

import javax.inject.Inject;
import javax.jms.JMSContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for all the job handlers that handle cluster messages.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class BaseJobMessageHandler {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected JMSContext context;

	/**
	 * Timeout when reading the queue = 2 minutes.
	 */
	protected static final Long TIMEOUT_DURATION = 120000L;

}
