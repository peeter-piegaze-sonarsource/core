package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.admin.job.cluster.message.queue.MediationJobQueuePublisher;
import org.meveo.admin.job.cluster.message.topic.ClusterJobTopicPublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;

/**
 * The Class MediationJobBean.
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 7.0
 * 
 */
@Stateless
public class MediationJobBean extends BaseJobBean {

    @Inject
    private ParamBeanFactory paramBeanFactory;
    
    @Inject
    private ClusterJobTopicPublisher clusterJobTopicPublisher;
    
    @Inject
    private MediationJobQueuePublisher mediationJobQueuePublisher;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}
		Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

		try {

			ParamBean parambean = paramBeanFactory.getInstance();
			String meteringDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator + "imports"
					+ File.separator + "metering" + File.separator;

			String inputDir = meteringDir + "input";
			String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
			ArrayList<String> cdrExtensions = new ArrayList<String>();
			cdrExtensions.add(cdrExtension);

			File f = new File(inputDir);
			if (!f.exists()) {
				f.mkdirs();
			}
			File[] files = FileUtils.listFiles(inputDir, cdrExtensions);
			if (files == null || files.length == 0) {
				return;
			}
			SubListCreator subListCreator = new SubListCreator(Arrays.asList(files), nbRuns.intValue());
			MeveoUser lastCurrentUser = currentUser.unProxy();
			String scriptCode = (String) this.getParamOrCFValue(jobInstance, "scriptJob");
			while (subListCreator.isHasNext()) {
				ClusterJobQueueDto queueDto = initClusterQueueDto(result, lastCurrentUser,
						new ArrayList<Long>(subListCreator.getNextWorkSet()));
				queueDto.setScriptCode(scriptCode);
				
				// send to queue
				mediationJobQueuePublisher.publishMessage(queueDto);
			}

			ClusterJobTopicDto clusterJobTopicDto = initClusterTopicDto(result.getJobInstance().getId(),
					RatedTransactionsJob.class.getSimpleName(), result.getId());

			clusterJobTopicPublisher.publishMessage(clusterJobTopicDto);

		} catch (Exception e) {
			log.error("Failed to run mediation", e);
			result.registerError(e.getMessage());
		}

	}

}