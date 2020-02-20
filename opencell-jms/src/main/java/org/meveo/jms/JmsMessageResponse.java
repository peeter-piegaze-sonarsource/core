package org.meveo.jms;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A response
 * 
 * @author Axione
 *
 */
public class JmsMessageResponse {
	public String getSource() {
		return source;
	}

	public LocalDateTime getAcknowledgeDate() {
		return acknowledgeDate;
	}

	public String getUid() {
		return uid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public String getErrorField() {
		return errorField;
	}

	public String getErrorValue() {
		return errorValue;
	}

	private String source;

	@JsonProperty("date")
	private LocalDateTime acknowledgeDate;

	private String uid;
	private String errorCode;
	private String errorDesc;
	private String errorField;
	private String errorValue;

	public void setSource(String source) {
		this.source = source;
	}

	public void setAcknowledgeDate(LocalDateTime acknowledgeDate) {
		this.acknowledgeDate = acknowledgeDate;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}

}
