package org.meveo.admin.job;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;

/**
 * The Class UnitFlatFileProcessingJobBean execute one line/record, in a new transaction.
 * 
 * @author anasseh
 * @lastModifiedVersion willBeSetLater
 */
@Stateless
public class UnitFlatFileProcessingJobBean {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
    /**
     * Execute one line/record, in a new transaction.
     * 
     * @param script script to execute
     * @param executeParams script context parameters
     * @throws BusinessException Business Exception
     */
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(ScriptInterface script, Map<String, Object> executeParams) throws BusinessException {
        executeParams.put(Script.CONTEXT_CURRENT_USER, currentUser);
        executeParams.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        script.execute(executeParams);
    }
}
