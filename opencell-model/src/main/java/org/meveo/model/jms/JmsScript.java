package org.meveo.model.jms;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;

import com.google.common.base.MoreObjects;

/**
 * The entity JmsScript associate a script and a jms queue
 * 
 * @author Axione
 *
 */
@Entity
@Cacheable
@ExportIdentifier("code")
@Table(name = "jms_script", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", 
                  parameters = { @Parameter(name = "sequence_name", value = "jms_script_seq"), })
public class JmsScript extends EnableBusinessEntity {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "queue_name", nullable = false, length = 255)
	@NotNull
	private String queueName;

	
	@Column(name = "script_code", nullable = false, columnDefinition = "TEXT")
	@NotNull
	private String scriptCode;
	
	@Column(name = "schema", nullable = false, length = 255)
    private String schema;

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("code", code)
                          .add("queue-name", queueName)
                          .add("schema", schema)
                          .toString();
    }
}

