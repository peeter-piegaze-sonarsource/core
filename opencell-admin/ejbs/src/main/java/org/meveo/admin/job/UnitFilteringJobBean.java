package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class UnitFilteringJobBean {

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Object obj, ScriptInterface scriptInterface, String recordVariableName) {

	Map<String, Object> context = new HashMap<String, Object>();
	context.put(recordVariableName, obj);
	try {
	    scriptInterface.execute(context);
	    result.registerSucces();
	} catch (BusinessException ex) {
	    result.registerError(ex.getMessage());
	}
    }
}
