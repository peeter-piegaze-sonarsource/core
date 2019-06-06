package org.meveo.admin.job.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class ClusterJobQueueDto implements Serializable {

	private static final long serialVersionUID = 2830144742632416757L;
	
	public static final String SCRIPT_CODE = "SCRIPT_CODE";

	private List<Serializable> items;
	private Map<String, Serializable> parameters;

	@Override
	public String toString() {
		return "ClusterJobQueueDto [items=" + items + ", parameters=" + parameters + "]";
	}

	public void addParameter(String key, Serializable val) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}

		if (val != null) {
			parameters.put(key, val.toString());
		}
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

}