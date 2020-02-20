package org.meveo.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * A JMS processor
 * 
 * @author Axione
 *
 */
public interface JmsMessageProcessor {
	
	/**
	 * Process a message
	 * 
	 * @param message - The message
	 * @return The message after processing 
	 * @throws JMSException - If error process message
	 */
	TextMessage process(TextMessage message) throws JMSException;

}
