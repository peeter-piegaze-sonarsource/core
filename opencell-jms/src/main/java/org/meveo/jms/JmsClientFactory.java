package org.meveo.jms;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The jms client factory
 * 
 * @author Axione
 */
@Stateless
@Named
public class JmsClientFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(JmsClientFactory.class);
	
	@Inject
	private JmsObjectMapperBean objectMapperBean;
	private ObjectMapper objectMapper;
	
	@Inject
	private JmsConnectionFactoryBean connectionFactory;
	
	private List<JmsClient> jmsClients = new ArrayList<>();
	
	/**
	 * Start the factory
	 */
	@PostConstruct
	public void startup() {
		objectMapper = objectMapperBean.objectMapper();
	}
	
	/**
	 * Shutdown close all client with her session and connection also
	 */
	@PreDestroy
	public void shutdown() {
	    jmsClients.forEach(JmsClient::close);
	}
	
	/**
	 * Create a new jms client
	 * 
	 * @return A Jms Client
	 */
	public JmsClient createJmsClient() {
		try {
			JmsClient jmsClient = new JmsClient(connectionFactory.createConnection())
                                      .withObjectMapper(objectMapper);
			
			LOGGER.debug("Jms Client Created");
			jmsClients.add(jmsClient);
			return jmsClient;
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
				   
	}

}
