package org.meveo.api.dto.jms;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * List og jms scriopt response
 * 
 * @author Axione
 */
@XmlRootElement(name = "JmsScriptListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JmsScriptListResponseDto extends BaseResponse {
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name = "jmsScripts")
    private List<JmsScriptDto> jmsScripts;

    public List<JmsScriptDto> getJmsScripts() {
        return jmsScripts;
    }

    public void setJmsScripts(List<JmsScriptDto> jmsScripts) {
        this.jmsScripts = jmsScripts;
    }

}
