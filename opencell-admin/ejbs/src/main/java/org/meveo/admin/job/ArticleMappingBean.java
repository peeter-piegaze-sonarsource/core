package org.meveo.admin.job;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class ArticleMappingBean extends BaseJobBean {
    @Inject
    private Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private ArticleMappingLineService articleMappingLineService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        try {
            List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "ArticleMappingJob_billingRun");
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "FirstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "LastTransactionDate");

            if (billingRunWrappers != null && (firstTransactionDate != null || lastTransactionDate != null)) {
                result.registerError("Can not set both billing run list and dates parameters");
                return;
            } else if (firstTransactionDate != null && lastTransactionDate != null && lastTransactionDate.before(firstTransactionDate)) {
                result.registerError("Invalid date range: first transaction date must be before last transaction date");
                return;
            } else if (billingRunWrappers != null) {
                List<Long> billingRunIds = billingRunWrappers.stream()
                        .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                        .collect(Collectors.toList());
                Map<String, Object> filters = new HashedMap();
                filters.put("inList id", billingRunIds);
                PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
                List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);

                List<RatedTransaction> ratedTransactions = billingRunService.loadRTsByBillingRuns(billingRuns);

                mapToArticle(ratedTransactions, result);
            } else {
                List<RatedTransaction> ratedTransactions = ratedTransactionService.listByStatusAndBetweenRange(RatedTransactionStatusEnum.OPEN, firstTransactionDate, lastTransactionDate);
                mapToArticle(ratedTransactions, result);
            }
        }catch(Exception exp){
            result.registerError(exp.getMessage());
            log.error("Failed to run article mapping job: " + exp);
        }
    }

    private void mapToArticle(List<RatedTransaction> ratedTransactions, JobExecutionResultImpl result) {
        log.info("Rated Transactions  to process={}", ratedTransactions.size());
        result.setNbItemsToProcess(ratedTransactions.size());

        List<ArticleMappingLine> articleMappingLines = articleMappingLineService.list();

        Optional<ArticleMapping> articleMapping = articleMappingLines.stream()
                .filter(ar -> ar.getArticleMapping() != null)
                .map(ArticleMappingLine::getArticleMapping)
                .findFirst();

        if(articleMapping.isPresent()){
            ratedTransactions = articleMappingLineService.executeArticleMappingScript(articleMapping.get().getCode(), ratedTransactions);
        } else {
            for (RatedTransaction ratedTransaction : ratedTransactions) {
                Set<AccountingArticle> accountingArticles = articleMappingLines.stream()
                        .filter(aml -> articleMappingLineService.match(aml, ratedTransaction))
                        .map(ArticleMappingLine::getAccountingArticle)
                        .collect(Collectors.toSet());
                ratedTransactionService.matchWithAccountingArticle(ratedTransaction, accountingArticles);
            }
        }
        long rejectedItems = ratedTransactions.stream()
                .filter(r -> r.getStatus() == RatedTransactionStatusEnum.REJECTED)
                .count();
        if(rejectedItems > 0){
            result.setNbItemsProcessedWithError(rejectedItems - 1);
            result.registerError("Article mapping incomplete");
            result.setNbItemsCorrectlyProcessed(ratedTransactions.size() - rejectedItems);
        }else {
            result.setNbItemsCorrectlyProcessed(ratedTransactions.size());
        }
    }
}
