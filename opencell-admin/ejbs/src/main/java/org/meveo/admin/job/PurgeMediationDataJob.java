package org.meveo.admin.job;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
/**
 * The Class job  to remove not open EDR, WO, RTx between two dates.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class PurgeMediationDataJob extends Job {

	/** The purge data job bean. */
    @Inject
    private PurgeMediationDataJobBean purgeMediationDataJobBean;
    
    public static final String APPLIES_TO_NAME = "JOB_PurgeMediationDataJob";

	public static final String PURGE_MEDIATION_DATA_JOB_PACKETS_NUMBER = "PurgeMediationDataJob_packetsNumber";

	public static final String PURGE_MEDIATION_DATA_JOB_DAYS_TO_PURGE = "PurgeMediationDataJob_daysToPurge";

	public static final String PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE = "PurgeMediationDataJob_lastTransactionDate";

	public static final String PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE = "PurgeMediationDataJob_firstTransactionDate";

	public static final String PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF = "PurgeMediationDataJob_rtStatusCf";

	public static final String PURGE_MEDIATION_DATA_JOB_RT_CF = "PurgeMediationDataJob_rtCf";

	public static final String PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF = "PurgeMediationDataJob_woStatusCf";

	public static final String PURGE_MEDIATION_DATA_JOB_WO_CF = "PurgeMediationDataJob_woCf";

	public static final String PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF = "PurgeMediationDataJob_edrStatusCf";

	public static final String PURGE_MEDIATION_DATA_JOB_EDR_CF = "PurgeMediationDataJob_edrCf";
	
	public static final String MESSAGE_PURGE_ENTITY_JOB_PACKETS_NUMBER = "purgeEntityJob.packetsNumber";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_RETAIN = "exportEntityJob.daysToRetain";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_LAST_TRANSACTION_DATE = "exportEntityJob.lastTransactionDate";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_FIRST_TRANSACTION_DATE = "exportEntityJob.firstTransactionDate";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF = "exportEntityJob.rtStatusCf";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_CF = "exportEntityJob.rtCf";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF = "exportEntityJob.woStatusCf";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_CF = "exportEntityJob.woCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF = "exportEntityJob.edrStatusCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_CF = "exportEntityJob.edrCf";
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        purgeMediationDataJobBean.execute(result, jobInstance);
    }
    
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }
    
    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        
    	Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
    	
        CustomFieldTemplate edrCf = new CustomFieldTemplate();
        edrCf.setCode(PURGE_MEDIATION_DATA_JOB_EDR_CF);
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_CF));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        edrCf.setDefaultValue("true");
        result.put(PURGE_MEDIATION_DATA_JOB_EDR_CF, edrCf);
        
        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        edrStatusCf.setValueRequired(false);
        edrStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF, edrStatusCf);
        
        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode(PURGE_MEDIATION_DATA_JOB_WO_CF);
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_CF));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        woCf.setDefaultValue("true");
        result.put(PURGE_MEDIATION_DATA_JOB_WO_CF, woCf);
        
        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        woStatusCf.setValueRequired(false);
        woStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF, woStatusCf);
        
        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode(PURGE_MEDIATION_DATA_JOB_RT_CF);
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_CF));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setDefaultValue("true");
        rtCf.setValueRequired(false);
        result.put(PURGE_MEDIATION_DATA_JOB_RT_CF, rtCf);
        
        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        rtStatusCf.setValueRequired(false);
        rtStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF, rtStatusCf);
        
        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode(PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE);
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_FIRST_TRANSACTION_DATE));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put(PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE, firstTransactionDate);
        
        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode(PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE);
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_LAST_TRANSACTION_DATE));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        result.put(PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE, lastTransactionDate);
        
        CustomFieldTemplate daysToPurge = new CustomFieldTemplate();
        daysToPurge.setCode(PURGE_MEDIATION_DATA_JOB_DAYS_TO_PURGE);
        daysToPurge.setAppliesTo(APPLIES_TO_NAME);
        daysToPurge.setActive(true);
        daysToPurge.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_RETAIN));
        daysToPurge.setFieldType(CustomFieldTypeEnum.LONG);
        daysToPurge.setValueRequired(false);
        daysToPurge.setDefaultValue("0");
        result.put(PURGE_MEDIATION_DATA_JOB_DAYS_TO_PURGE, daysToPurge);
        
        CustomFieldTemplate paquetsNumber = new CustomFieldTemplate();
        paquetsNumber.setCode(PURGE_MEDIATION_DATA_JOB_PACKETS_NUMBER);
        paquetsNumber.setAppliesTo(APPLIES_TO_NAME);
        paquetsNumber.setActive(true);
        paquetsNumber.setDescription(resourceMessages.getString(MESSAGE_PURGE_ENTITY_JOB_PACKETS_NUMBER));
        paquetsNumber.setFieldType(CustomFieldTypeEnum.LONG);
        paquetsNumber.setValueRequired(true);
        paquetsNumber.setDefaultValue("100");
        result.put(PURGE_MEDIATION_DATA_JOB_PACKETS_NUMBER, paquetsNumber);
        
        return result;
        
    }
    
}