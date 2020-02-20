package org.meveo.api.dto.jms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * Response of base jsmScript
 * 
 * @author Axione
 */
@XmlRootElement(name = "JmsScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JmsScriptResponseDto extends BaseResponse {
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	private JmsScriptDto jmsScriptDto;

	public JmsScriptDto getJmsScriptDto() {
		return jmsScriptDto;
	}

	public void setJmsScriptDto(JmsScriptDto jmsScriptDto) {
		this.jmsScriptDto = jmsScriptDto;
	}
	
	

}
