package org.meveo.service.finance;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.service.base.PersistenceService;

/**
 * Service for managing ReportExtractExecutionResult.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 24 Apr 2018
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class ReportExtractExecutionResultService extends PersistenceService<ReportExtractExecutionResult> {

	/**
	 * Creates and commits a new ReportExtractExecutionResult in a new transaction.
	 * 
	 * @param reportExtractExecutionResult Result of ReportExtract execution.
	 * @throws BusinessException Business exception.
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createInNewTransaction(ReportExtractExecutionResult reportExtractExecutionResult) throws BusinessException {
		create(reportExtractExecutionResult);
	}

}
