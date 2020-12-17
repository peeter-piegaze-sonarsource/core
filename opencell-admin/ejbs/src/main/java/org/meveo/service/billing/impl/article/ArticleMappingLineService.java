package org.meveo.service.billing.impl.article;

import org.meveo.model.BusinessEntity;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.ejb.Stateless;
import java.util.HashMap;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

    public boolean match(ArticleMappingLine articleMappingLine, RatedTransaction ratedTransaction) {
        boolean elMatch = false;
        boolean parametersMatch = false;
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
        return articleMappingLine.matchWithAll() || elMatch || parametersMatch
                || match(articleMappingLine.getOfferTemplate(), ratedTransaction.getOfferTemplate())
                || (ratedTransaction.getChargeInstance() != null && match(articleMappingLine.getProductTemplate(), ratedTransaction.getChargeInstance().getChargeTemplate()))
                || (ratedTransaction.getChargeInstance() != null && match(articleMappingLine.getChargeTemplate(), ratedTransaction.getChargeInstance().getChargeTemplate()));
    }

    private boolean match(BusinessEntity businessEntity1, BusinessEntity businessEntity2) {
        return businessEntity1 != null && businessEntity2 != null && businessEntity1.getId().equals(businessEntity2.getId());
    }


}
