package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.GenericWorkflowAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.filter.FilterService;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.slf4j.Logger;

@Stateless
public class GenericWorkflowJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private GenericWorkflowAsync genericWorkflowAsync;

    @Inject
    private FilterService filterService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            String genericWfCode = null;
            try {
                genericWfCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "gwfJob_generic_wf")).getCode();
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            GenericWorkflow genericWf = genericWorkflowService.findByCode(genericWfCode);
            if (genericWf == null) {
                throw new BusinessException(String.format("No Workflow found with code = [%s]", genericWfCode));
            }

            if (!genericWf.isActive()) {
                log.debug("The workflow " + genericWfCode + " is disabled, the job will exit.");
                return;
            }
            
            // Create wf instances for entities without WF
            List<BusinessEntity> entitiesWithoutWFInstance = workflowInstanceService.findEntitiesForWorkflow(genericWf, true);
            for (BusinessEntity entity : entitiesWithoutWFInstance) {
                workflowInstanceService.create(entity, genericWf);
            }
            
            Filter wfFilter = genericWf.getFilter();
            Filter filter = null;
            if(wfFilter!=null) {
            	filter = filterService.findById(wfFilter.getId());
            }
            
            List<BusinessEntity> entities = null;
            if (filter != null) {
            	entities = (List<BusinessEntity>) filterService.filteredListAsObjects(filter);
            } else {
            	entities = workflowInstanceService.findEntitiesForWorkflow(genericWf, false);
            }

            List<WorkflowInstance> wfInstances = genericWorkflowService.findByCode(genericWfCode, Arrays.asList("wfInstances")).getWfInstances();

            if (genericWf.getId() != null) {
                genericWf = genericWorkflowService.refreshOrRetrieve(genericWf);
            }
            
            Map<Long, BusinessEntity> mapFilteredEntities = entities.stream().collect(Collectors.toMap(x->x.getId(), x->x));
            
            SubListCreator subListCreator = new SubListCreator(wfInstances, nbRuns.intValue());
            List<Map<Long, List<Object>>> subMaps = new ArrayList();
            while (subListCreator.isHasNext()) {
            	Map<Long, List<Object>> wfInstancesFiltered = new TreeMap();
            	List<WorkflowInstance> nextWorkSet = (List<WorkflowInstance>) subListCreator.getNextWorkSet();
				for (WorkflowInstance workflowInstance : nextWorkSet ){
                    Long entityInstanceId = workflowInstance.getEntityInstanceId();
					if (entityInstanceId != null) {
                        BusinessEntity businessEntity = mapFilteredEntities.get(entityInstanceId);
    					if(businessEntity != null) {
                            wfInstancesFiltered.put(entityInstanceId, Arrays.asList(businessEntity,workflowInstance));
                        }
                    }
                }
            	subMaps.add(wfInstancesFiltered);
            }
            
            log.debug("wfInstances:" + wfInstances.size());
            result.setNbItemsToProcess(wfInstances.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            for(Map<Long, List<Object>> map:subMaps) {
                futures.add(genericWorkflowAsync.launchAndForget(map, genericWf, result, lastCurrentUser));

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
            log.error("Failed to run generic workflow job", e);
            result.registerError(e.getMessage());
        }
    }
}
