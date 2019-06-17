package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

import java.util.List;

public class GetReccuringChargeTemplateListResponseDto extends SearchResponse {
    
    private List<RecurringChargeTemplateDto> recurringChargeTemplateDtos;
    
    public List<RecurringChargeTemplateDto> getRecurringChargeTemplateDtos() {
        return recurringChargeTemplateDtos;
    }
    
    public void setRecurringChargeTemplateDtos(List<RecurringChargeTemplateDto> recurringChargeTemplateDtos) {
        this.recurringChargeTemplateDtos = recurringChargeTemplateDtos;
    }
}
