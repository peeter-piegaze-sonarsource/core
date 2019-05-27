package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.admin.job.cluster.message.queue.RecurringRatingJobQueuePublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private Logger log;

    @Inject
    @Rejected
    private Event<Serializable> rejectededChargeProducer;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    private RecurringRatingJobQueuePublisher recurringRatingJobQueuePublisher;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("start in running with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }

        try {
            Date rateUntilDate = null;
            try {
                rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
            } catch (Exception e) {
                log.warn("Cant get customFields for {} {}", jobInstance.getJobTemplate(), e.getMessage());
            }
            if (rateUntilDate == null) {
                rateUntilDate = new Date();
            }

            List<Long> ids = recurringChargeInstanceService.findIdsByStatus(InstanceStatusEnum.ACTIVE, rateUntilDate, false);
            int inputSize = ids.size();
            result.setNbItemsToProcess(inputSize);
            log.info("RecurringRatingJob - charges to rate={}", inputSize);

            SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
//                ClusterJobQueueDto queueDto = initClusterQueueDto(result, lastCurrentUser, new ArrayList<Long>(subListCreator.getNextWorkSet()));
//    			queueDto.setRateUntilDate(rateUntilDate);
//
//    			// send to queue
//    			recurringRatingJobQueuePublisher.publishMessage(queueDto);
            }
            
//            ClusterJobTopicDto clusterJobTopicDto = initClusterTopicDto(result.getJobInstance().getId(), RatedTransactionsJob.class.getSimpleName(), result.getId());
//                        
//            clusterJobTopicPublisher.publishMessage(clusterJobTopicDto);
        } catch (Exception e) {
            log.error("Failed to run recurring rating job", e);
            result.registerError(e.getMessage());
        }
        log.debug("end running RecurringRatingJobBean!");
    }
}
