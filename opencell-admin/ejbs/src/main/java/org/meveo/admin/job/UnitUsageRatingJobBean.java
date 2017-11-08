package org.meveo.admin.job;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitUsageRatingJobBean {

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    @Rejected
    private Event<Serializable> rejectededEdrProducer;

    @EJB
    private UnitUsageRatingJobBean unitUsageRatingJobBean;

    // @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long edrId) throws BusinessException {
    	long startDate = System.currentTimeMillis();
        log.debug("Running with edrId={}", edrId);
        EDR edr = null;
        try {
            edr = edrService.findById(edrId);
            log.debug("After findById:" + (System.currentTimeMillis() - startDate));
            if (edr == null) {
                return;
            }
            usageRatingService.ratePostpaidUsage(edr);
            
            log.debug("After ratePostpaidUsage:" + (System.currentTimeMillis() - startDate));
            
            if (edr.getStatus() == EDRStatusEnum.RATED) {
                edr = edrService.updateNoCheck(edr);
                log.debug("After updateNoCheck:" + (System.currentTimeMillis() - startDate));
                result.registerSucces();
                log.debug("After registerSucces:" + (System.currentTimeMillis() - startDate));
            } else {
                throw  new BusinessException(edr.getRejectReason());
            }
        } catch (BusinessException e) {
            if (!(e instanceof InsufficientBalanceException)) {
                log.error("Failed to unit usage rate for {}", edrId, e);
            }
            throw e;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerFailedEdr(JobExecutionResultImpl result, Long edrId, Exception e) throws BusinessException {
	EDR edr = edrService.findById(edrId);  
	edr.setStatus(EDRStatusEnum.REJECTED);
	edr.setRejectReason(StringUtils.truncate(e.getMessage(), 255, true));
        rejectededEdrProducer.fire(edr);
        result.registerError(edr.getId(), e != null ? e.getMessage() : edr.getRejectReason());
        result.addReport("EdrId : " + edr.getId() + " RejectReason : " + (e != null ? e.getMessage() : edr.getRejectReason()));
    }
}