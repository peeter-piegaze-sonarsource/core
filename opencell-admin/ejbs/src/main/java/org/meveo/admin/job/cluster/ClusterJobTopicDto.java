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
	private String currentUserName;
	private String providerCode;

	@Override
	public String toString() {
		return "ClusterJobTopicDto [jobInstanceId=" + jobInstanceId + ", jobTemplate=" + jobTemplate
				+ ", jobExecutionResultImplId=" + jobExecutionResultImplId + ", sourceNode=" + sourceNode
				+ ", currentUserName=" + currentUserName + ", providerCode=" + providerCode + "]";
	}

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

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

}
