/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
import org.slf4j.Logger;

@Stateless
public class XMLInvoiceGenerationJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoicingAsync invoicingAsync;

    @Inject
    private JobExecutionErrorService jobExecutionErrorService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String parameter, JobInstance jobInstance) {

        jobExecutionErrorService.purgeJobErrors(jobInstance);

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            List<String> statusNamesList = (List<String>) this.getParamOrCFValue(jobInstance, "invoicesToProcess", Arrays.asList("VALIDATED"));
			List<InvoiceStatusEnum> statusList = statusNamesList.stream().map(status->InvoiceStatusEnum.valueOf(status)).collect(Collectors.toList());

            Long billingRunId = null;
            if (parameter != null && parameter.trim().length() > 0) {
                try {
                    billingRunId = Long.parseLong(parameter);
                } catch (Exception e) {
                    log.error("error while getting billing run", e);
                    result.registerError(e.getMessage());
                }
            }

            List<Long> invoiceIds = this.fetchInvoiceIdsToProcess(statusList, billingRunId);

            log.info("invoices to process={}", invoiceIds == null ? null : invoiceIds.size());
            List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
            SubListCreator subListCreator = new SubListCreator(invoiceIds, nbRuns.intValue());
            result.setNbItemsToProcess(subListCreator.getListSize());

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(invoicingAsync.generateXmlAsync((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<Boolean> future : futures) {
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
            log.error("Failed to generate XML invoices", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

    private List<Long> fetchInvoiceIdsToProcess(List<InvoiceStatusEnum> statusList, Long billingRunId) {
        log.debug(" fetchInvoiceIdsToProcess for InvoiceStatusEnums = {} and billingRunId = {} ", statusList, billingRunId);
        return invoiceService.listInvoicesWithoutXml(billingRunId, statusList);
    }
}