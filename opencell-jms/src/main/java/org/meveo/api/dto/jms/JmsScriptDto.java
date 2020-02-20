package org.meveo.api.dto.jms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.jms.JmsScript;

/**
 * The jms script dto
 * 
 * @author Axione
 *
 */
@XmlRootElement(name = "JmsScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class JmsScriptDto extends EnableBusinessDto {
	private static final long serialVersionUID = 1L;

	@XmlElement(required = true)
	private String queueName;
	
	@XmlElement(required = true)
	private String scriptCode;
	
	@XmlElement(required = false)
    private String schema;
    
	
	public JmsScriptDto() {
		super();
	}
	
    public JmsScriptDto(JmsScript jmsScript) {
    	super(jmsScript);
    	queueName = jmsScript.getQueueName();
    	scriptCode = jmsScript.getScriptCode();
    	schema = jmsScript.getSchema();
    }
    

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
