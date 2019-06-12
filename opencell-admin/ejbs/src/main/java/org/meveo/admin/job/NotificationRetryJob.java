/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.notification.NotificationHistoryService;

/**
 * Tries again the failed notifications that were marked to be retried
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.3
 */
@Stateless
public class NotificationRetryJob extends Job {

    /**
     * Handles notification processing
     */
    @Inject
    private NotificationRetryJobBean notificationReplayJobBean;

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @EJB
    private NotificationRetryJob notificationReplayJobAsync;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        Long nrRetry = (Long) this.getParamOrCFValue(jobInstance, "nrRetry", 3L);

        try {
            MeveoUser lastCurrentUser = currentUser.unProxy();

            List<Long> historyToProcess = notificationHistoryService.getHistoryToRetry();

            result.setNbItemsToProcess(historyToProcess.size());

            List<Future<String>> futures = new ArrayList<>();
            SubListCreator<Long> subListCreator = new SubListCreator(historyToProcess, nbRuns.intValue());

            while (subListCreator.isHasNext()) {
                futures
                    .add(notificationReplayJobAsync.launchAndForget(nrRetry, (List<Long>) subListCreator.getNextWorkSet(), result, jobInstance.getParametres(), lastCurrentUser));
                if (subListCreator.isHasNext()) {
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
            log.error("Failed to run notification history replay", e);
            result.registerError(e.getMessage());
        }
    }

    /**
     * Retry notifications
     * 
     * @param nrRetry Number of times to retry. Exceeding the value, notification will be marked as failed.
     * @param ids A list of notification history ids
     * @param result Job execution result
     * @param parameter Execution parameters
     * @param lastCurrentUser Current user
     * @return Future execution object
     * @throws BusinessException Business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(Long nrRetry, List<Long> ids, JobExecutionResultImpl result, String parameter, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (Long historyId : ids) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            notificationReplayJobBean.retryNotification(result, historyId, nrRetry);
        }
        return new AsyncResult<>("OK");
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JobInstance_NotificationRetryJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:0");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_NotificationRetryJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:1");
        result.put("waitingMillis", customFieldNbWaiting);

        CustomFieldTemplate nrRetry = new CustomFieldTemplate();
        nrRetry.setCode("nrRetry");
        nrRetry.setAppliesTo("JobInstance_NotificationRetryJob");
        nrRetry.setActive(true);
        nrRetry.setDescription("Number of times notification should be retried before being marked as failed.)");
        nrRetry.setFieldType(CustomFieldTypeEnum.LONG);
        nrRetry.setValueRequired(true);
        nrRetry.setDefaultValue("5");
        nrRetry.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:2");
        result.put("nrRetry", nrRetry);
        return result;
    }
}