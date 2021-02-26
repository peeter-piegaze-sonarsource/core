package org.meveo.api.catalog;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixValueService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;
    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    public PricePlanMatrixLineDto addPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) throws MeveoApiException, BusinessException {

        checkCommunMissingParameters(dtoData);

        return pricePlanMatrixLineService.createPricePlanMatrixLine(dtoData);
    }
    
    public GetPricePlanVersionResponseDto addPricePlanMatrixLines(PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
            for (PricePlanMatrixLineDto pricePlanMatrixLineDto:dtoData.getPricePlanMatrixLinesDto()) {
            	addPricePlanMatrixLine(pricePlanMatrixLineDto);
            }
           PricePlanMatrixVersion ppmVersion= pricePlanMatrixLineService.getPricePlanMatrixVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
          return new GetPricePlanVersionResponseDto(ppmVersion);
    }

    
    public GetPricePlanVersionResponseDto updatePricePlanMatrixLines(PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
        PricePlanMatrixVersion ppmVersion= pricePlanMatrixLineService.getPricePlanMatrixVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        	ppmVersion.getLines().clear();
        	Set<PricePlanMatrixLine> lines = new HashSet<PricePlanMatrixLine>();
            for (PricePlanMatrixLineDto pricePlanMatrixLineDto:dtoData.getPricePlanMatrixLinesDto()) {
            	PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
            	pricePlanMatrixLine.setPricetWithoutTax(pricePlanMatrixLineDto.getPricetWithoutTax());
                pricePlanMatrixLine.setPriority(pricePlanMatrixLineDto.getPriority());
                pricePlanMatrixLine.getPricePlanMatrixValues().clear();
                pricePlanMatrixLine.setPricePlanMatrixVersion(ppmVersion);
                pricePlanMatrixLine.setDescription(pricePlanMatrixLineDto.getDescription());
                pricePlanMatrixLineService.create(pricePlanMatrixLine);
                Set<PricePlanMatrixValue> pricePlanMatrixValues = pricePlanMatrixLineService.getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
                pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
                lines.add(pricePlanMatrixLine);
            }
            ppmVersion.getLines().addAll(lines);
            pricePlanMatrixVersionService.update(ppmVersion);
          return new GetPricePlanVersionResponseDto(ppmVersion);
    }
    
    public PricePlanMatrixLineDto updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {

        if(StringUtils.isBlank(pricePlanMatrixLineDto.getPpmLineId()))
            missingParameters.add("ppmLineId");
        checkCommunMissingParameters(pricePlanMatrixLineDto);


        return pricePlanMatrixLineService.updatePricePlanMatrixLine(pricePlanMatrixLineDto);
    }

    private void checkCommunMissingParameters(PricePlanMatrixLineDto dtoData) {
        if(StringUtils.isBlank(dtoData.getPricePlanMatrixCode())){
            missingParameters.add("pricePlanMatrixCode");
        }
        if(StringUtils.isBlank(dtoData.getPricePlanMatrixVersion())){
            missingParameters.add("pricePlanMatrixVersion");
        }

        dtoData.getPricePlanMatrixValues().stream()
                .forEach(value -> {
                    if(StringUtils.isBlank(value.getPpmColumnCode())){
                        missingParameters.add("pricePlanMatrixValues.ppmColumnCode");
                    }
                });

        handleMissingParametersAndValidate(dtoData);
    }

    public void remove(Long ppmLineId) {
        if(StringUtils.isBlank(ppmLineId))
            missingParameters.add("pricePlanMatrixLineId");
        handleMissingParameters();
        PricePlanMatrixLine ppmLine = pricePlanMatrixLineService.findById(ppmLineId);
        if(ppmLine == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, ppmLineId);
        }
        ppmLine.getPricePlanMatrixVersion().getLines().remove(ppmLine);
        pricePlanMatrixVersionService.update(ppmLine.getPricePlanMatrixVersion());
    }

    public PricePlanMatrixLineDto load(Long ppmLineId){
        if(StringUtils.isBlank(ppmLineId))
            missingParameters.add("pricePlanMatrixLineId");
        handleMissingParameters();
        return pricePlanMatrixLineService.load(ppmLineId);
    }
}
