package org.meveo.jms;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.BusinessApiException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Send responde of proceesing message to sender
 * 
 * @author Axione
 */
public class JmsMessageResponder {
	private JmsClient jmsClient;
	private String responseQueue;
	
	/**
	 * Construct the responder
	 * 
	 * @param jmsClient - The client
	 * @param reponseQueue - The response queue 
	 */
	public JmsMessageResponder(JmsClient jmsClient, String responseQueue) {
	   this.jmsClient = jmsClient;
	   this.responseQueue = responseQueue;
	}
	
	/**
	 * Construct the responses
	 * 
	 * @param message - The source message
	 * @throws JMSException 
	 * @throws IOException - Cna 't get uid
	 * @throws JsonProcessingException 
	 */
	private JmsMessageResponse jmsMessageResponse(TextMessage textMessage) throws JMSException, JsonProcessingException, IOException {
		JmsMessageResponse response = new JmsMessageResponse();
		response.setAcknowledgeDate(LocalDateTime.now());
		    	
		if (textMessage != null) {
		 String text = textMessage.getText();
		 
		  if (StringUtils.isEmpty(text)) {
		   response.setSource(text);
		   String uid = jmsClient.getObjectMapper().readTree(text).get(JmsConstants.UID).asText();
		   response.setUid(uid);
		  }
		 }
		
		return response;
	}

	/**
	 * Process the exception
	 * 
	 * @param error - The exception
	 * @param response - Response
	 * 
	 * @return The reponse
	 */
	private JmsMessageResponse processException(Exception error, JmsMessageResponse response) {
		if (error == null) {
			return response;
		}
		
		if (error instanceof BusinessApiException) {
			BusinessApiException businessException = (BusinessApiException) error;
			response.setErrorCode(businessException.getErrorCode().name());
		}
		
		response.setErrorDesc(error.getMessage());
		
		return response;
	}
	
	/**
	 * Reponse to sender
	 * 
	 * @param message - The message
	 * @param error - The exception
	 */
	public void respond(TextMessage message, Exception error) {
		JmsMessageResponse response = null;
		try {
			response = processException(error, jmsMessageResponse(message));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		jmsClient.convertAndSend(responseQueue, response);
	}

}
