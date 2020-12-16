package org.meveo.service.billing.impl.article;

import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.ejb.Stateless;
import java.util.HashMap;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

    public boolean match(ArticleMappingLine articleMappingLine, RatedTransaction ratedTransaction) {
        if(articleMappingLine.getMappingKelEL() != null){
            HashMap<Object, Object> contextMap = new HashMap<>();
            contextMap.put("ratedTransaction", ratedTransaction);
            return ValueExpressionWrapper.evaluateToBoolean(articleMappingLine.getMappingKelEL(), contextMap);
        }
        if(articleMappingLine.getParameter1() != null || articleMappingLine.getParameter2() != null || articleMappingLine.getParameter3() != null){
            boolean param1Match = articleMappingLine.getParameter1() != null ? articleMappingLine.getParameter1().equals(ratedTransaction.getParameter1()) : ratedTransaction.getParameter1() == null;
            boolean param2Match = articleMappingLine.getParameter2() != null ? articleMappingLine.getParameter2() .equals(ratedTransaction.getParameter2()) : ratedTransaction.getParameter2() == null;
            boolean param3Match = articleMappingLine.getParameter3()  != null ? articleMappingLine.getParameter3() .equals(ratedTransaction.getParameter3()) : ratedTransaction.getParameter3() == null;
            return param1Match && param2Match && param3Match;
        }
        return true;
    }


}
