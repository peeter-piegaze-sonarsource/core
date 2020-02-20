package org.meveo.jms;


/**
 * Ack for read message
 * 
 * @author Axione
 */
public class JmsMessageAcknowledgment {
	
	/**
	 * Status of ack
	 */
	public enum Status {
		/**
		 * Pending
		 */
		PENDING,
		/**
		 * Ok
		 */
		OK,
		/**
		 * Ko
		 */
		KO
	}
	
	private String message;
	private Status status = Status.PENDING;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
