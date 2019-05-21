package org.meveo.admin.job.cluster;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class ClusterJobQueueDto implements Serializable {

	private static final long serialVersionUID = 2830144742632416757L;
	private JobExecutionResultImpl jobExecutionResultImpl;
	private MeveoUser meveoUser;
	private List<Long> ids;
	private String sourceNode;

	private RatedTransactionsJobAggregationSetting ratedTransactionsJobAggregationSetting;
	private Date invoicingDate;
	
	private Date rateUntilDate;
	
	/** MediationJob */
	private String parameters;
	private String scriptCode;

	public RatedTransactionsJobAggregationSetting getRatedTransactionsJobAggregationSetting() {
		return ratedTransactionsJobAggregationSetting;
	}

	public void setRatedTransactionsJobAggregationSetting(
			RatedTransactionsJobAggregationSetting ratedTransactionsJobAggregationSetting) {
		this.ratedTransactionsJobAggregationSetting = ratedTransactionsJobAggregationSetting;
	}

	public JobExecutionResultImpl getJobExecutionResultImpl() {
		return jobExecutionResultImpl;
	}

	public void setJobExecutionResultImpl(JobExecutionResultImpl jobExecutionResultImpl) {
		this.jobExecutionResultImpl = jobExecutionResultImpl;
	}

	public MeveoUser getMeveoUser() {
		return meveoUser;
	}

	public void setMeveoUser(MeveoUser meveoUser) {
		this.meveoUser = meveoUser;
	}

	public Date getInvoicingDate() {
		return invoicingDate;
	}

	public void setInvoicingDate(Date invoicingDate) {
		this.invoicingDate = invoicingDate;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}

	public Date getRateUntilDate() {
		return rateUntilDate;
	}

	public void setRateUntilDate(Date rateUntilDate) {
		this.rateUntilDate = rateUntilDate;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

}
