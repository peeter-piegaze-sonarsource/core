package org.meveo.admin.job.importexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.async.ImportCustomersAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.Job;

/**
 * @author phung
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ImportCustomersJob extends Job {

    @Inject
    private ImportCustomersAsync importCustomersAsync;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (int i = 0; i < nbRuns.intValue(); i++) {
                futures.add(importCustomersAsync.launchAndForget(result, lastCurrentUser));
                if (i > 0) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to import customers", e);
            result.registerError(e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JobInstance_ImportCustomersJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setValueRequired(false);
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_ImportCustomersJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("500");
        customFieldNbWaiting.setValueRequired(false);
        result.put("waitingMillis", customFieldNbWaiting);

        return result;
    }
}