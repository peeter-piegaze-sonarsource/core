package org.meveo.admin.job;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.cluster.ClusterJobQueueDto;
import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.admin.job.cluster.message.queue.MediationJobQueuePublisher;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;

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
    private MediationJobQueuePublisher mediationJobQueuePublisher;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		String scriptCode = (String) this.getParamOrCFValue(jobInstance, "scriptJob");
		
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}

		try {

			ParamBean parambean = paramBeanFactory.getInstance();
			String meteringDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator + "imports"
					+ File.separator + "metering" + File.separator;

			String inputDir = meteringDir + "input";
			String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
			ArrayList<String> cdrExtensions = new ArrayList<>();
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
			
			while (subListCreator.isHasNext()) {
				List<Serializable> filePaths = new ArrayList<>();
				Iterator it = subListCreator.getNextWorkSet().iterator();
				while (it.hasNext()) {
					File inFile = (File) it.next();
					filePaths.add(inFile.getAbsolutePath());
				}

				ClusterJobQueueDto queueDto = initClusterQueueDto(result, filePaths);
				queueDto.addParameter(ClusterJobQueueDto.SCRIPT_CODE, scriptCode);

				// send to queue
				mediationJobQueuePublisher.publishMessage(queueDto);
			}

			ClusterJobTopicDto clusterJobTopicDto = initClusterTopicDto(result);

			clusterJobTopicPublisher.publishMessage(clusterJobTopicDto);

		} catch (Exception e) {
			log.error("Failed to run mediation", e);
			result.registerError(e.getMessage());
		}

	}

}