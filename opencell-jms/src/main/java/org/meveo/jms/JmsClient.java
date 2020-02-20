package org.meveo.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * The JMS Client
 * 
 * @author Axione
 *
 */
public class JmsClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(JmsClient.class);
	
	private static final String TEXT_PROPERTY = "_text";
	
	private Connection connection;
	private boolean transacted = false;
	private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
	private ObjectMapper objectMapper;
	private Session session;

	private Map<String, Destination> destinations = new HashMap<>();
	private Map<String, MessageProducer> messageProducers = new HashMap<>();
	private Map<String, MessageConsumer> messageConsumers = new HashMap<>();
	
	private JmsMessageProcessor messageProcessor;
	
	/**
	 * Construct the client
	 * 
	 * @param connection - The connection
	 */
	public JmsClient(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Change the transacted
	 * 
	 * @param transacted - The transacted (on/off)
	 * @return himself
	 */
	public JmsClient withTransacted(boolean transacted) {
		this.transacted = transacted;
		return this;
	}

	/**
	 * Change the achnowledge mode
	 * 
	 * @oparam acknowledgeMode - The acknowledge mode
	 * @return himself
	 */
	public JmsClient withAcknowledgeMode(int acknowledgeMode) {
		this.acknowledgeMode = acknowledgeMode;
		return this;
	}

    /**
     * Define the object mapper
     * 
     * @param objectMapper - The object mapper
     * @return himself
     */
	public JmsClient withObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	/**
	 * Define the message processor
	 * 
	 * @param messageProcessor - The message processor
	 * @return himself
	 */
	public JmsClient withMessageProcessor(JmsMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
		return this;
	}

	/**
	 * Return the object mapper
	 * 
	 * @return object mapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
	
	/**
	 * Create a session
	 * 
	 * @param transacted - The transacted
	 * @param acknowledgeMode - The acknowledgeMode
	 * 
	 * @return The session
	 */
	public Session createSession(boolean transacted, int acknowledgeMode) {
		try {
			connection.start();
			Session session = connection.createSession(transacted, acknowledgeMode);
			LOGGER.debug("Create jms session {}", session);
			return session;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Create a session with defined transacted adn acknowledge mode
	 * 
	 * @return The session
	 */
	public Session createSession() {
		return createSession(transacted, acknowledgeMode);
	}
	
	/**
	 * Return the session
	 * 
	 * @return The current session (create a new one if need)
	 */
	public Session session() {
		if (session == null) {
			session = createSession();
		}
		
		return session;
	}
	
	/**
	 * Create a destination
	 * 
	 * @param destinationName - The destination name
	 * @return The destination 
	 */
	public Destination createDestination(String destinationName) {
		try {
			Destination destination = session().createQueue(destinationName);
			LOGGER.debug("JMS Queue {} created", destination);
			return destination;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Return the current destination (create a new one if need)
     * 
     * @param destinationName - Name for destination
     * @return destination
     */
	public Destination destination(String destinationName) {
		return destinations.computeIfAbsent(destinationName, k -> createDestination(destinationName));
	}
	
	/**
	 * Get a destination
	 * 
	 * @param destination name
	 * @return The destination or null
	 */
	public Destination getDestinationByName(String destinationName) {
		return destinations.get(destinationName);
	}
	
	/**
	 * Create a message producer
	 * 
	 * @param destinationName - The destination name
	 * @return The message producer
	 */
	public MessageProducer createMessageProducer(String destinationName) {
		try {
			MessageProducer messageProducer = session.createProducer(destination(destinationName));
			LOGGER.debug("Message consumer for queue {} created", messageProducer);
			return messageProducer;
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return a message producer (create a new one if need)
	 * 
	 * @param destination - The destination
	 * @return A message producer
	 */
	public MessageProducer messageProducer(String destinationName) {
		return messageProducers.computeIfAbsent(destinationName, k -> createMessageProducer(destinationName));
	}
	
	/**
	 * Create a text message
	 * 
	 * 
	 * @param text - The text
	 * @return A text message
	 */
	public TextMessage createTextMessage(String text) {
		try {
			return session().createTextMessage(text);
		}
		catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
	
	/**
	 * Create a message text from object
	 * 
	 * Use JSOn mapper
	 * 
	 * @param object - The object
	 * @return A text mapper  
	 */
	public TextMessage createTextMessageFromObject(Object object) {
		Assert.notNull(objectMapper, "Object Mapper is null !!");
		try {
			TextMessage textMessage = createTextMessage(objectMapper.writeValueAsString(object));
			textMessage.setStringProperty(TEXT_PROPERTY, object.getClass().getName());
			return textMessage;
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
	
	/**
	 * Convert and send the message
	 * 
	 * @param destinationName - The destination name
	 * @param object - Object ot send
	 * @param messageProcessor - The message processor
	 * 
	 * @return The message sended
	 */
	public TextMessage convertAndSend(String destinationName, Object object, JmsMessageProcessor messageProcessor) {
		TextMessage textMessage = createTextMessageFromObject(object);
		
		if (messageProcessor != null) {
			try {
				textMessage = messageProcessor.process(textMessage);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}
		
		try {
			messageProducer(destinationName).send(textMessage);
			LOGGER.debug("JMS message sended on queue {},", destinationName);
			return textMessage;
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convert and send object
	 * 
	 * @param destinationName - The destination name
	 * @param object - Object ot send
	 * 
	 * @return The message sended
	 */
	public TextMessage convertAndSend(String destinationName, Object object) {
		return convertAndSend(destinationName, object, messageProcessor);
    }
	
	/**
	 * Create a message consumer
	 * 
	 * @param destinationName - Name iof destination
	 * @param messageListener - The messae listener
	 * 
	 * @return The message listener
	 */
	public MessageConsumer createMessageConsumer(String destinationName)  {
		try {
		 MessageConsumer messageConsumer = session().createConsumer(destination(destinationName));
		 return messageConsumer;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return the message consumer
	 * 
	 * @param destinationName - The destination name
	 * @return The message consumer
	 */
	public MessageConsumer messageConsumer(String destinationName, MessageListener messageListener) {
		MessageConsumer messageConsumer = messageConsumers.computeIfAbsent(destinationName, k -> createMessageConsumer(destinationName));
		try {
			messageConsumer.setMessageListener(messageListener);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		return messageConsumer;
	}
	
	/**
	 * Get message consumer on queue
	 * 
	 * @param queueName - Name of queue
	 * @return The consumer
	 */
	public MessageConsumer getMessageConsumerOnDestination(String destinationName) {
		return messageConsumers.get(destinationName);
	}

	/**
	 * Close the client
	 */
	public void close() {
	    if (session != null) {
	        try {
                session.close();
            }
            catch (JMSException e) {
                throw new RuntimeException(e);    
            }
	    }
	    
	    try {
            connection.close();
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
	}
}
