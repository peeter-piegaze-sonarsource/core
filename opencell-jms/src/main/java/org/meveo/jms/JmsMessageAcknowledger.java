package org.meveo.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.meveo.jms.JmsMessageAcknowledgment.Status;

/**
 * Send ack to sender
 * 
 * @author Axione
 *
 */
public class JmsMessageAcknowledger {
	private JmsClient jmsClient;
	
	/**
	 * Construct the acknowledger
	 * 
	 * @param jmsClient - The JMS Client
	 */
	public JmsMessageAcknowledger(JmsClient jmsClient) {
		this.jmsClient = jmsClient;
	}
	
	/**
	 * Acknowledge a message
	 * 
	 * @param message - The message to acknowledge
	 * @param error - error of ack
	 */
	public void acknowledge(TextMessage textMessage, String error) {
		
		try {
			String replyTo = textMessage.getStringProperty(JmsConstants.REPLY_TO);
			String correlationID = textMessage.getStringProperty(JmsConstants.CORRELATION_ID);
			JmsMessageProcessor messageProcessor = new JmsMessageProcessor() {
				
				@Override
				public TextMessage process(TextMessage message) throws JMSException {
					message.setJMSCorrelationID(correlationID);
					return message;
				}
			};
			
			JmsMessageAcknowledgment ack = new JmsMessageAcknowledgment();
			ack.setStatus(Status.OK);
			
			if (StringUtils.isNotBlank(error)) {
				ack.setStatus(Status.KO);
				ack.setMessage(error);
			}
			
			jmsClient.convertAndSend(replyTo, ack, messageProcessor);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
