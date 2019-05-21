package org.meveo.admin.job;

import java.util.ArrayList;

import javax.inject.Inject;

import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.admin.job.cluster.message.topic.ClusterJobTopicPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BaseJobBean : Holding a common behaviors for all JoBbeans instances
 */
public abstract class BaseJobBean {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;
    
    @Inject
    protected ClusterJobTopicPublisher clusterJobTopicPublisher;

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @param defaultValue Default value if no value found
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode, Object defaultValue) {
        Object value = getParamOrCFValue(jobInstance, cfCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }

    /**
     * Initialize the queue dto.
     * @param result job execution result
     * @param lastCurrentUser currently logged user
     * @param arrayList arrayList of ids
     * @return initialized ClusterJobQueueDto
     */
	protected ClusterJobQueueDto initClusterQueueDto(JobExecutionResultImpl result, MeveoUser lastCurrentUser, ArrayList<Long> ids) {
		ClusterJobQueueDto queueDto = new ClusterJobQueueDto();
		queueDto.setJobExecutionResultImpl(result);
		queueDto.setMeveoUser(lastCurrentUser);
		queueDto.setIds(ids);
		queueDto.setParameters(result.getJobInstance().getParametres());

		return queueDto;
	}
	
	/**
	 * Initialize the topic dto
	 * @param jobInstanceId id of job
	 * @param templateName class name of job
	 * @param jobExecutionResultImplId execution result id of job
	 * @return initialized ClusterJobTopicDto
	 */
	protected ClusterJobTopicDto initClusterTopicDto(Long jobInstanceId, String templateName,
			Long jobExecutionResultImplId) {
		ClusterJobTopicDto topicDto = new ClusterJobTopicDto();
		topicDto.setJobInstanceId(jobInstanceId);
		topicDto.setJobTemplate(templateName);
		topicDto.setJobExecutionResultImplId(jobExecutionResultImplId);

		return topicDto;
	}
}
