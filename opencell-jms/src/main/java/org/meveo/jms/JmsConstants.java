package org.meveo.jms;

/**
 * The JMS Headers
 * 
 * @author Axione
 *
 */
public interface JmsConstants {
     /**
      *
      */
	public static final String REPLY_TO = "replyTo";
	
	/**
	 * The correlation id headers
	 */
	public static final String CORRELATION_ID = "jms_correlationId";
	
	/**
	 * The UID
	 */
	public static final String UID = "uid";

	/**
	 * Content KEY 
	 */
	public static final String CONTENT_KEY = "MESSAGE";
	
	/**
	 * Header key
	 */
	public static final String HEADERS_KEY = "HEADER";
	
	/**
	 * Schema key
	 */
	public static final String SCHEMA_KEY = "schema";
	
	/**
	 * No schema on this queue
	*/
	public static final String NO_SCHEMA_KEY= "no-schema";
    
}
