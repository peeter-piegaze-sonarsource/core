package org.meveo.jms;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.MessageConsumer;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.jms.JmsScript;
import org.meveo.service.jms.impl.JmsScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The jms message script factory
 * 
 * @author Axione
 *
 */
@Startup
@Stateless
@Named
public class JmsMessageScriptFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessageScriptFactory.class);

	private String responseQueue = ParamBean.getInstance()
	                                        .getProperty("activemq.queue.response", "");
	
	private boolean acknowledgment = Boolean.parseBoolean(ParamBean.getInstance()
			                                                       .getProperty("activemq.acknowledgement", "false"));

	@Inject
	private JmsClientFactory jmsClientFactory;

	@Inject
	private JmsObjectMapperBean objectMapperBean;
	
	@Inject
	private JmsMessageScriptListenerExecutor jmsScriptExecutor;
	
	@Inject
	private JmsScriptService jmsScriptService;

	private JmsMessageAcknowledger acknowledger;
	private JmsMessageResponder responder;

	private Map<String, MessageConsumer> messageConsumers = new HashMap<>();
	private Map<String, JmsMessageScriptListener> messageListeners = new HashMap<>();
	private JmsClient jmsClient;

	/**
	 * Create a jms client
	 */
	private JmsClient jmsClient() {
		if (jmsClient == null) {
		 jmsClient = jmsClientFactory.createJmsClient()
				                     .withObjectMapper(objectMapperBean.objectMapper());
		}
		
		return jmsClient;
	}

	/**
	 * Init
	 */
	@PostConstruct
	public void configure() {
	    LOGGER.debug("Configure JMS script listener");

		if (acknowledgment) {
			acknowledger = new JmsMessageAcknowledger(jmsClient());
		}

		if (StringUtils.isNotBlank(responseQueue)) {
			responder = new JmsMessageResponder(jmsClient(), responseQueue);
		}
		
		startup();
	}
	
	/**
	 * Create the listener
	 * @return The listener
	 */
	private JmsMessageScriptListener createJmsMessageScriptListener() {
		return new JmsMessageScriptListener().withAcknowledger(acknowledger)
				                             .withResponder(responder)
				                             .withExecutor(jmsScriptExecutor);
	}
	
	/**
	 * Create a message consumer on queue
	 * 
	 * @param destinationName- The destination name
	 * 
	 * @return The consumer
	 */
	public MessageConsumer createMessageConsumer(String destinationName) {
		LOGGER.debug("Create a message scripts listener on queue {}", destinationName);
		JmsMessageScriptListener scriptListener = messageListeners.computeIfAbsent(destinationName, k -> createJmsMessageScriptListener());
		return jmsClient().messageConsumer(destinationName, scriptListener);
	}

	/**
	 * Return the message consumer
	 * 
	 * @param destinationName- Queue name
	 * @param scriptCodes - Script code for this queue (one by schema)
	 * 
	 * @return The script listener for this queue
	 */
	public MessageConsumer messageConsumer(String destinationName) {
		return messageConsumers.computeIfAbsent(destinationName, k -> createMessageConsumer(k));
	}
	
	/**
	 * Attach map of schema/script on queue
	 * 
	 * @param destinationName - The queue name
	 * @param schemaScriptCodes - The schema script codes
	 * 
	 * @return The message 
	 */
	public JmsMessageScriptListener messageListenerWithShemaScriptCodes(String destinationName, Map<String, String> schemaScriptCodes) {
	    JmsMessageScriptListener scriptListener = messageListeners.get(destinationName);
	    
	    if (scriptListener == null) {
	        throw new IllegalArgumentException("No listener on queue : [" + destinationName + "]");
	    }
	    
	    return scriptListener.withSchemaScriptCodes(schemaScriptCodes);
	}
	
	/**
     * Attach map of schema/script on queue
     * 
     * @param destinationName - The queue name
     * @param schemaScriptCodes - The schema script codes
     * 
     * @return The message 
     */
    public JmsMessageScriptListener messageListenerWithShemaScriptCode(String destinationName, String schema, String scriptCode) {
        JmsMessageScriptListener scriptListener = messageListeners.get(destinationName);
        
        if (scriptListener == null) {
            throw new IllegalArgumentException("No listener on queue : [" + destinationName + "]");
        }
        
        return scriptListener.withSchemaScriptCode(schema, scriptCode);
    }
    
    /**
     * Attach the script to jms
     * 
     * @param jms - The jms
     */
    public void attachScript(JmsScript jmsScript) {
        LOGGER.debug("Attach script : {}", jmsScript);
        messageConsumer(jmsScript.getQueueName());
        messageListenerWithShemaScriptCode(jmsScript.getQueueName(), jmsScript.getSchema(), jmsScript.getScriptCode());
    }
    
    /**
     * Detach a script
     * 
     * @param script - the script
     */
    public void detachScript(JmsScript jmsScript) {
       String destinationName = jmsScript.getQueueName();
       JmsMessageScriptListener scriptListener = messageListeners.get(destinationName);
        
        if (scriptListener == null) {
            throw new IllegalArgumentException("No listener on queue : [" + destinationName + "]");
        }
        
        scriptListener.withoutSchema(jmsScript.getSchema());
    }
	
	/**
	 * Start all listeners
	 */
	public void startup() {
	   LOGGER.debug("Start up jms script listener factory");
	   jmsScriptService.list()
	                   .forEach(this::attachScript);
	}

}