/**
 * 
 */
package org.meveo.admin.async;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitRecurringRatingJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RecurringChargeAsync {

    @Inject
    private Logger log;

    @Inject
    private UnitRecurringRatingJobBean unitRecurringRatingJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Process recurring charges of given recuring charge instances. Once charge instance at a time in a separate transaction.
     * 
     * @param ids A list of recurring charge instance ids
     * @param result Job execution result
     * @param maxDate Process untill date
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, Date maxDate, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (Long id : ids) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitRecurringRatingJobBean.execute(result, id, maxDate);
        }
        log.debug("End launchAndForget!");

        return new AsyncResult<String>("OK");
    }
}