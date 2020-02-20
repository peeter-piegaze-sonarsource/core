package org.meveo.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * A Jms Message script
 * 
 * @author Axione
 *
 */
public class JmsMessageScriptListener implements MessageListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessageScriptListener.class);
	private Map<String, String> schemaScriptCodes = new HashMap<>();
	private JmsMessageAcknowledger acknowledger;
	private JmsMessageResponder responder;
	private JmsMessageScriptListenerExecutor executor;
	
	/**
	 * Define the responder
	 * 
	 * @param responder - The responder
	 */
	public JmsMessageScriptListener withResponder(JmsMessageResponder responder) {
		this.responder = responder;
		return this;
	}
	
	/**
	 * Define the acknowledger
	 * 
	 * @param acknowledger - The acknowledger
	 * @return himsef
	 */
	public JmsMessageScriptListener withAcknowledger(JmsMessageAcknowledger acknowledger) {
		this.acknowledger = acknowledger;
		return this;
	}
	
	/**
	 * Define the executor
	 * 
	 * @param executor - The executor
	 * @return himself
	 */
	public JmsMessageScriptListener withExecutor(JmsMessageScriptListenerExecutor executor) {
	    this.executor = executor;
	    return this;
	}
	
	/**
	 * With schema scripts codes
	 * 
	 * @param scriptCodes - The script codes
	 * @return himself
	 */
	public JmsMessageScriptListener withSchemaScriptCodes(Map<String, String> schemaScriptCodes) {
       Assert.notEmpty(schemaScriptCodes,"Schema script code map is empty !!!");
       this.schemaScriptCodes = schemaScriptCodes;
       
       return this;
 	}
	
	/**
	 * With a new schema script code
	 * 
	 * @param schema - The schema
	 * @param scriptCode - The script code
	 */
	public JmsMessageScriptListener  withSchemaScriptCode(String schema, String scriptCode) {
        Assert.hasText(schema, "Schema is empty or null !!!");
        Assert.hasText(scriptCode, "Script Code is empty or null !!!");
        
        LOGGER.debug("Add schema [{}] / script code []", scriptCode);
        
	    schemaScriptCodes.put(schema, scriptCode);
	    
	    return this;
    }
	
	/**
	 * Without this schema
	 */
	public JmsMessageScriptListener withoutSchema(String schema) {
	    Assert.hasText(schema, "Schema is empty or null !!!");
        schemaScriptCodes.remove(schema);
        
        return this;
	}
	
	@Override
	public void onMessage(Message message) {
		Assert.notNull(message, "JMS Message is null !!!");
		Assert.isTrue(message instanceof TextMessage, "JMS Message is not a textMessage !!!");
		
		TextMessage textMessage = (TextMessage) message;
	    LOGGER.debug("Receive message {}", message);

		
		if (acknowledger != null) {
			acknowledger.acknowledge(textMessage, null);
		}

		Map<String, Object> context = messageToMap(textMessage);
		Exception error = null;
		
		String scriptCode = scriptCode(context);
		
		
		try {
		   	executor.executeScript(scriptCode, context);
		}
		catch (Exception e) {
		    LOGGER.warn("Error during message processing : {}", e);
			error = e;
		}

		if (responder != null) {
			responder.respond(textMessage, error);
		}
		
	}

	/**
	 * Build a map form message
	 * 
	 * Tow element content & headers
	 * 
	 * @param textMessage - The text message
	 * @return A map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> messageToMap(TextMessage textMessage) {
		Map<String, Object> map = new HashMap<>();
		
		try {
			map.put(JmsConstants.CONTENT_KEY, textMessage.getText());
			Map<String, String> headers = new HashMap<>();
			
			Enumeration<String> propertyNames = textMessage.getPropertyNames();
			
			while (propertyNames.hasMoreElements()) {
				String propertyName = propertyNames.nextElement();
				String propertyValue = textMessage.getStringProperty(propertyName);
				headers.put(propertyName, propertyValue);
			}
			
			map.put(JmsConstants.HEADERS_KEY, headers);
		}
		catch (Exception e) {
		 	throw new RuntimeException(e);
		}
		
		return map;
	}
	
	/**
	 * Get script code for message
	 * 
	 * Get schema and get script from configuration
	 * 
	 * @param context - The context
	 * @return The script code for message
	 */
	@SuppressWarnings("unchecked")
    private String scriptCode(Map<String, Object> context) {
	    Map<String, String> headers = (Map<String, String>) context.get(JmsConstants.HEADERS_KEY);
	    String scriptKey = null;
	    
	    if (headers == null || headers.isEmpty() || !headers.containsKey(JmsConstants.SCHEMA_KEY)) {
	        scriptKey = JmsConstants.NO_SCHEMA_KEY;
	    }
	    else {
	        scriptKey = headers.get(JmsConstants.SCHEMA_KEY);
	    }
	    
	    String scriptCode = schemaScriptCodes.get(scriptKey);
	    
	    if (scriptCode == null) {
	        throw new RuntimeException("Can't find script for schema [" + scriptKey + "]");
	    }
	    
	    
	    return scriptCode;
	}
	

}
