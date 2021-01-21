package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class PricePlanMatrixVersionApi extends BaseCrudApi<PricePlanMatrixVersion, PricePlanMatrixVersionDto> {

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Override
    public PricePlanMatrixVersion create(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException, BusinessException {
        return createOrUpdate(pricePlanMatrixVersionDto);
    }

    @Override
    public PricePlanMatrixVersion update(PricePlanMatrixVersionDto dtoData) throws MeveoApiException, BusinessException {
        return createOrUpdate(dtoData);
    }

    public void removePricePlanMatrixVersion(String pricePlanMatrixCode, int currentVersion) {
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        handleMissingParameters();

        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            pricePlanMatrixVersionService.removePriceMatrixVersion(pricePlanMatrixVersion);
        } catch (BusinessException exp) {
            throw new MeveoApiException(exp);
        }
    }

    @Override
    public PricePlanMatrixVersion createOrUpdate(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        Boolean isMatrix = pricePlanMatrixVersionDto.getMatrix();
        int currentVersion = pricePlanMatrixVersionDto.getVersion();
        String pricePlanMatrixCode = pricePlanMatrixVersionDto.getPricePlanMatrixCode();

        if (StringUtils.isBlank(isMatrix)) {
            missingParameters.add("isMatrix");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        handleMissingParametersAndValidate(pricePlanMatrixVersionDto);

        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);

        if (pricePlanMatrixVersion == null) {
            pricePlanMatrixVersion = populatePricePlanMatrixVersion(new PricePlanMatrixVersion(), pricePlanMatrixVersionDto, VersionStatusEnum.DRAFT, Calendar.getInstance().getTime());
            pricePlanMatrixVersionService.create(pricePlanMatrixVersion);
        } else {
            populatePricePlanMatrixVersion(pricePlanMatrixVersion, pricePlanMatrixVersionDto, pricePlanMatrixVersionDto.getStatusEnum(), pricePlanMatrixVersionDto.getStatusDate());
            pricePlanMatrixVersionService.updatePricePlanMatrixVersion(pricePlanMatrixVersion);
        }
        pricePlanMatrixService.update(pricePlanMatrixVersion.getPricePlanMatrix());

        if(pricePlanMatrixVersionDto.getLines() != null && !pricePlanMatrixVersionDto.getLines().isEmpty()){
            pricePlanMatrixVersionDto.getLines().forEach(this::checkCommunMissingParameters);
            removeLinesToRemove(pricePlanMatrixVersionDto, pricePlanMatrixVersion);
            pricePlanMatrixLineService.createOrUpdateLines(pricePlanMatrixVersionDto.getLines());
        } else if(pricePlanMatrixVersion.getLines() != null && !pricePlanMatrixVersion.getLines().isEmpty()) {
            pricePlanMatrixLineService.removeAll(pricePlanMatrixVersion.getLines().stream().collect(Collectors.toSet()));
        }
        return pricePlanMatrixVersion;
    }

    private void removeLinesToRemove(PricePlanMatrixVersionDto pricePlanMatrixVersionDto, PricePlanMatrixVersion pricePlanMatrixVersion) {
        Set<Long> linesToUpdate = pricePlanMatrixVersionDto.getLines()
                .stream()
                .filter(l -> l.getPpmLineId() != null)
                .map(PricePlanMatrixLineDto::getPpmLineId)
                .collect(Collectors.toSet());

        Set<PricePlanMatrixLine> linesToRemove = pricePlanMatrixVersion.getLines()
                .stream()
                .filter(l -> !linesToUpdate.contains(l.getId()))
                .collect(Collectors.toSet());

        if(!linesToRemove.isEmpty())
            pricePlanMatrixLineService.removeAll(linesToRemove);
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

    private PricePlanMatrixVersion populatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrixVersionDto pricePlanMatrixVersionDto, VersionStatusEnum status, Date statusTime) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        if (pricePlanMatrix == null)
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        pricePlanMatrixVersion.setCurrentVersion(pricePlanMatrixVersionDto.getVersion());
        pricePlanMatrixVersion.setValidity(pricePlanMatrixVersionDto.getValidity());
        if (status != null)
            pricePlanMatrixVersion.setStatus(status);
        if (statusTime != null)
            pricePlanMatrixVersion.setStatusDate(statusTime);
        pricePlanMatrixVersion.setPriceWithoutTax(pricePlanMatrixVersionDto.getPriceWithoutTax());
        pricePlanMatrixVersion.setMatrix(pricePlanMatrixVersionDto.getMatrix());
        pricePlanMatrixVersion.setLabel(pricePlanMatrixVersionDto.getLabel());
        pricePlanMatrix.getVersions().add(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public GetPricePlanVersionResponseDto updateProductVersionStatus(String pricePlanMatrixCode, int currentVersion, VersionStatusEnum status) {
        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            pricePlanMatrixVersionService.updateProductVersionStatus(pricePlanMatrixVersion, status);
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersion);
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }

    public GetPricePlanVersionResponseDto duplicateProductVersion(String pricePlanMatrixCode, int currentVersion) {

        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersionService.duplicate(pricePlanMatrixVersion));
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }
}
