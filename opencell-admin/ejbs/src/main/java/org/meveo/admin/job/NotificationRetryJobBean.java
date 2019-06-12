package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.notification.DefaultNotificationService;
import org.slf4j.Logger;

/**
 * Handles notification retry job processing
 */
@Stateless
public class NotificationRetryJobBean extends BaseJobBean {

    /** The log. */
    @Inject
    protected Logger log;

    @Inject
    private DefaultNotificationService defaultNotificationService;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void retryNotification(JobExecutionResultImpl result, Long notificationHistoryId, Long nrRetry) {
        try {

            defaultNotificationService.retryNotification(notificationHistoryId, nrRetry);
            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to retry notification {}", notificationHistoryId, e);
            result.registerError(e.getMessage());
        }
    }

}