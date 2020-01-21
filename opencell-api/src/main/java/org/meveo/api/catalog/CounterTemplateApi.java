package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CounterTemplateApi extends BaseCrudApi<CounterTemplate, CounterTemplateDto> {

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private CalendarService calendarService;

    @Override
    public CounterTemplate create(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        if (counterTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        CounterTemplate counterTemplate = new CounterTemplate();
        counterTemplate.setCode(postData.getCode());
        counterTemplate.setDescription(postData.getDescription());
        counterTemplate.setUnityDescription(postData.getUnity());
        if (postData.getType() != null) {
            counterTemplate.setCounterType(postData.getType());
        }
        counterTemplate.setCeiling(postData.getCeiling());
        if (postData.isDisabled() != null) {
            counterTemplate.setDisabled(postData.isDisabled());
        }
        counterTemplate.setCalendar(calendar);
        if (postData.getCounterLevel() != null) {
            counterTemplate.setCounterLevel(postData.getCounterLevel());
        }
        counterTemplate.setCeilingExpressionEl(postData.getCeilingExpressionEl());
        counterTemplate.setNotificationLevels(postData.getNotificationLevels());
        counterTemplate.setAccumulator(postData.getAccumulator());
        if (counterTemplate.getAccumulator() != null && counterTemplate.getAccumulator()) {
            counterTemplate.setCeilingExpressionEl(null);
            counterTemplate.setCeiling(BigDecimal.ZERO);
        }
        counterTemplateService.create(counterTemplate);

        return counterTemplate;
    }

    @Override
    public CounterTemplate update(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        CounterTemplate counterTemplate = counterTemplateService.findByCode(postData.getCode());
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }
        counterTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        counterTemplate.setDescription(postData.getDescription());
        counterTemplate.setUnityDescription(postData.getUnity());
        if (postData.getType() != null) {
            counterTemplate.setCounterType(postData.getType());
        }
        counterTemplate.setCeiling(postData.getCeiling());
        counterTemplate.setCalendar(calendar);
        if (postData.getCounterLevel() != null) {
            counterTemplate.setCounterLevel(postData.getCounterLevel());
        }
        counterTemplate.setCeilingExpressionEl(postData.getCeilingExpressionEl());
        counterTemplate.setNotificationLevels(postData.getNotificationLevels());
        counterTemplate.setAccumulator(postData.getAccumulator());
        if (counterTemplate.getAccumulator() != null && counterTemplate.getAccumulator()) {
            counterTemplate.setCeilingExpressionEl(null);
            counterTemplate.setCeiling(BigDecimal.ZERO);
        }
        counterTemplate = counterTemplateService.update(counterTemplate);

        return counterTemplate;
    }

    @Override
    public CounterTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        return new CounterTemplateDto(counterTemplate);
    }

    /**
     * Check if any parameters are missing and throw and exception.
     *
     * @param dto base data transfer object.
     * @throws MeveoApiException meveo api exception.
     */
    @Override
    protected void handleMissingParametersAndValidate(BaseEntityDto dto) throws MeveoApiException {
        validate(dto);
        handleMissingParameters(dto);
        CounterTemplateDto counterTemplateDto = (CounterTemplateDto) dto;
        if (counterTemplateDto.getAccumulator() != null) {
            if (counterTemplateDto.getAccumulator() && counterTemplateDto.getType().equals(CounterTypeEnum.NOTIFICATION)) {
                log.error("The counter type is invalid if the counter is accumulator counter, deactivate the accumulator or change the counter type");
                throw new InvalidParameterException("The counter type is invalid if the counter is accumulator counter, deactivate the accumulator or change the counter type");
            }
            if (!counterTemplateDto.getAccumulator() && (counterTemplateDto.getType().equals(CounterTypeEnum.USAGE_AMOUNT))) {
                log.error("The accumulator should be activated if the following counter type are used : {}, {} or {}", CounterTypeEnum.USAGE_AMOUNT.getLabel(),
                        CounterTypeEnum.USAGE_AMOUNT.getLabel(), CounterTypeEnum.USAGE_AMOUNT.getLabel());
                throw new InvalidParameterException("The accumulator must be activated to use the counter type " + counterTemplateDto.getType());
            }
        }
    }
}