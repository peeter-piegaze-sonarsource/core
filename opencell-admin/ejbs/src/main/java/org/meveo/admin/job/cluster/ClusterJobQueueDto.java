package org.meveo.admin.job.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Queue message for clustering jobs.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class ClusterJobQueueDto implements Serializable {

	private static final long serialVersionUID = 2830144742632416757L;

	public static final String SCRIPT_CODE = "SCRIPT_CODE";
	public static final String START_DATE = "START_DATE";
	public static final String END_DATE = "END_DATE";

	private Long jobInstanceId;
	private String jobTemplate;
	private Long jobExecutionResultImplId;
	private String sourceNode;
	private String currentUserName;
	private String providerCode;
	private int deliveryCount = 1;

	private Integer nbThreads;
	private List<Serializable> items;
	private Map<String, Serializable> parameters;

	@Override
	public String toString() {
		return "ClusterJobQueueDto [jobInstanceId=" + jobInstanceId + ", jobTemplate=" + jobTemplate
				+ ", jobExecutionResultImplId=" + jobExecutionResultImplId + ", sourceNode=" + sourceNode
				+ ", currentUserName=" + currentUserName + ", providerCode=" + providerCode + ", deliveryCount="
				+ deliveryCount + ", nbThreads=" + nbThreads + ", items=" + items + ", parameters=" + parameters + "]";
	}

	public boolean isValid() {
		return getDeliveryCount() >= 1 && getDeliveryCount() <= 3;
	}

	public void addParameter(String key, Serializable val) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}

		if (val != null) {
			parameters.put(key, val.toString());
		}
	}

	public Object getParameter(String key) {
		if (parameters != null) {
			return parameters.get(key);
		}

		return null;
	}

	public List<Serializable> getItems() {
		return items;
	}

	public void setItems(List<Serializable> items) {
		this.items = items;
	}

	public Map<String, Serializable> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Serializable> parameters) {
		this.parameters = parameters;
	}

	public Integer getNbThreads() {
		return nbThreads;
	}

	public void setNbThreads(Integer nbThreads) {
		this.nbThreads = nbThreads;
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

	public int getDeliveryCount() {
		return deliveryCount;
	}

	public void setDeliveryCount(int deliveryCount) {
		this.deliveryCount = deliveryCount;
	}

}