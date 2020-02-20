package org.meveo.jms;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A object mapper bean for JSON seraialization
 * 
 * @author Axione
 *
 */
@Stateless
@Named
public class JmsObjectMapperBean {

	private ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * Configure the object maper
	 */
	@PostConstruct
	public void configure() {
		 objectMapper.registerModule(new JavaTimeModule());
		 objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	/**
	 * Return the object mapper
	 */
	public ObjectMapper objectMapper() {
		return objectMapper;
	}
}
