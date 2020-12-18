package org.meveo.service.billing.impl.article;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public boolean match(ArticleMappingLine articleMappingLine, RatedTransaction ratedTransaction) {
        boolean elMatch = true;
        boolean parametersMatch = true;
        if(articleMappingLine.getMappingKelEL() != null){
            HashMap<Object, Object> contextMap = new HashMap<>();
            contextMap.put("ratedTransaction", ratedTransaction);
            elMatch = ValueExpressionWrapper.evaluateToBoolean(articleMappingLine.getMappingKelEL(), contextMap);
        }
        if(articleMappingLine.getParameter1() != null || articleMappingLine.getParameter2() != null || articleMappingLine.getParameter3() != null){
            boolean param1Match = articleMappingLine.getParameter1() != null ? articleMappingLine.getParameter1().equals(ratedTransaction.getParameter1()) : ratedTransaction.getParameter1() == null;
            boolean param2Match = articleMappingLine.getParameter2() != null ? articleMappingLine.getParameter2() .equals(ratedTransaction.getParameter2()) : ratedTransaction.getParameter2() == null;
            boolean param3Match = articleMappingLine.getParameter3()  != null ? articleMappingLine.getParameter3() .equals(ratedTransaction.getParameter3()) : ratedTransaction.getParameter3() == null;
            parametersMatch = param1Match && param2Match && param3Match;
        }
        return articleMappingLine.matchWithAll() || (elMatch && parametersMatch
                && offerTemplateMatch(articleMappingLine.getOfferTemplate(), ratedTransaction.getOfferTemplate())
                && productTemplateMatch(articleMappingLine.getProductTemplate(), ratedTransaction.getChargeInstance())
                && chargeTemplateMatch(articleMappingLine.getChargeTemplate(), ratedTransaction.getChargeInstance()));
    }

    private boolean offerTemplateMatch(OfferTemplate articleMappingLineOfferTemplate, OfferTemplate ratedTransactionOfferTemplate) {
        return articleMappingLineOfferTemplate == null || (ratedTransactionOfferTemplate != null && ratedTransactionOfferTemplate.getId().equals(articleMappingLineOfferTemplate.getId()));
    }

    private boolean productTemplateMatch(ProductTemplate productTemplate, ChargeInstance chargeInstance) {
        return productTemplate == null || (chargeInstance != null && chargeInstance.getChargeTemplate() != null && productTemplate.getId().equals(chargeInstance.getChargeTemplate().getId()));
    }

    private boolean chargeTemplateMatch(ChargeTemplate chargeTemplate, ChargeInstance chargeInstance) {
        return chargeTemplate == null || (chargeInstance != null && chargeInstance.getChargeTemplate() != null && chargeTemplate.getId().equals(chargeInstance.getChargeTemplate().getId()));
    }

    @SuppressWarnings("unchecked")
    public List<RatedTransaction> executeArticleMappingScript(String scriptInstanceCode, List<RatedTransaction> ratedTransactions) throws BusinessException {

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        context.put("ratedTransactions", ratedTransactions);
        scriptInstanceService.executeCached(scriptInstanceCode, context);
        return (List<RatedTransaction>) context.get(Script.RESULT_VALUE);
    }


}
