package org.meveo.admin.job.cluster;

import java.io.Serializable;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class ClusterJobTopicDto implements Serializable {

	private static final long serialVersionUID = 1899465570289395837L;

	private Long jobInstanceId;
	private String jobTemplate;
	private Long jobExecutionResultImplId;
	private String sourceNode;

	public Long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public String getJobTemplate() {
		return jobTemplate;
	}

	public void setJobTemplate(String jobTemplate) {
		this.jobTemplate = jobTemplate;
	}

	public Long getJobExecutionResultImplId() {
		return jobExecutionResultImplId;
	}

	public void setJobExecutionResultImplId(Long jobExecutionResultImplId) {
		this.jobExecutionResultImplId = jobExecutionResultImplId;
	}

	public String getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}

}
