package org.meveo.admin.job;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.script.finance.ReportExtractScript;
import org.slf4j.Logger;

/**
 * Extension class for JobExtractReport to start a new transaction.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class UnitReportExtractJobBean {

	private ParamBean paramBean = ParamBean.getInstance();

	@Inject
	private Logger log;

	@Inject
	private ReportExtractService reportExtractService;

	@JpaAmpNewTx
	// @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, Long id, Date startDate, Date endDate) {

		try {
			ReportExtract entity = reportExtractService.findById(id);

			if (startDate != null) {
				entity.getParams().put(ReportExtractScript.START_DATE,
						DateUtils.formatDateWithPattern(startDate, paramBean.getDateFormat()));
			}
			if (endDate != null) {
				entity.getParams().put(ReportExtractScript.END_DATE,
						DateUtils.formatDateWithPattern(endDate, paramBean.getDateFormat()));
			}

			reportExtractService.runReport(entity, null, ReportExtractExecutionOrigin.JOB);

			result.registerSucces();

		} catch (Exception e) {
			log.error("Failed to generate acount operations", e);
			result.registerError(e.getMessage());
		}
	}

}
