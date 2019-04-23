package org.meveo.service.api;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldFormattedValueDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.commons.utils.AppliesToValuesCalculator;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.mediation.Access;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * The Class EntityToDtoConverter.
 * 
 * @author Abdellatif BARI
 * @author melyoussoufi
 * @lastModifiedVersion 8.0
 */
@Stateless
public class EntityToDtoConverter {

	/** The logger. */
	@Inject
	private Logger logger;

	/** The custom field template service. */
	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	/** The custom entity instance service. */
	@Inject
	private CustomEntityInstanceService customEntityInstanceService;

	/** The app provider. */
	@Inject
	@ApplicationProvider
	protected Provider appProvider;

	static boolean accumulateCF = true;

	@PostConstruct
	private void init() {
		accumulateCF = Boolean
				.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("accumulateCF", "false"));
	}

	/**
	 * Gets the custom fields DTO.
	 *
	 * @param entity the entity
	 * @return the custom fields DTO
	 */
	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity) {
		return getCustomFieldsDTO(entity, CustomFieldInheritanceEnum.INHERIT_NONE);
	}

	/**
	 * Gets the custom fields DTO.
	 *
	 * @param entity    the entity
	 * @param inheritCF the inherit CF
	 * @return the custom fields DTO
	 */
	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, CustomFieldInheritanceEnum inheritCF) {
		return getCustomFieldsDTO(entity, null, inheritCF);
	}

	/**
	 * Gets the custom fields DTO.
	 *
	 * @param entity    the entity
	 * @param cfValues  the cf values
	 * @param inheritCF the inherit CF
	 * @return the custom fields DTO
	 */
	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldValue>> cfValues,
			CustomFieldInheritanceEnum inheritCF) {

		if (entity == null) {
			return null;
		}

		if (inheritCF == null) {
			inheritCF = CustomFieldInheritanceEnum.INHERIT_NO_MERGE;
		}

		// Accumulated type is allowed only when value accumulation is turned on
		if (inheritCF == CustomFieldInheritanceEnum.ACCUMULATED && !accumulateCF) {
			inheritCF = CustomFieldInheritanceEnum.INHERIT_NO_MERGE;
		}

		if (cfValues == null && entity.getCfValues() != null) {
			cfValues = entity.getCfValues().getValuesByCode();
		}

		if (cfValues == null && inheritCF == CustomFieldInheritanceEnum.INHERIT_NONE) {
			return null;
		}

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);

		// logger.trace("Get Custom fields for \nEntity: {}/{}\nCustom Field Values:
		// {}", entity.getClass().getSimpleName(), ((IEntity) entity).getId(),
		// cfValues);

		// In case of INHERIT_MERGED scenario current values are merged with inherited
		// values
		if (accumulateCF && inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED) {
			if (entity.getCfAccumulatedValues() != null) {
				for (Entry<String, List<CustomFieldValue>> cfValueInfo : entity.getCfAccumulatedValues()
						.getValuesByCode().entrySet()) {
					String cfCode = cfValueInfo.getKey();
					// Return only those values that have cft
					if (!cfts.containsKey(cfCode)) {
						continue;
					}
					for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
						if (!cfValue.isExcessiveInSize()) {
							currentEntityCFs.getCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
						}
					}
				}
			}

		} else {
			if (cfValues != null && !cfValues.isEmpty()) {
				for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValues.entrySet()) {
					String cfCode = cfValueInfo.getKey();
					// Return only those values that have cft
					if (!cfts.containsKey(cfCode)) {
						continue;
					}
					for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
						if (!cfValue.isExcessiveInSize()) {
							currentEntityCFs.getCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
						}
					}
				}
			}
		}

		// Add parent CF values if inherited

		// In case of ACCUMULATED scenario, inherited values contain merged current and
		// inherited values
		if (inheritCF == CustomFieldInheritanceEnum.ACCUMULATED) {

			if (entity.getCfAccumulatedValues() != null) {
				for (Entry<String, List<CustomFieldValue>> cfValueInfo : entity.getCfAccumulatedValues()
						.getValuesByCode().entrySet()) {
					String cfCode = cfValueInfo.getKey();
					// Return only those values that have cft
					if (!cfts.containsKey(cfCode)) {
						continue;
					}
					// Add only those that are really inherited values
					for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
						if (!cfValue.isExcessiveInSize()) {
							currentEntityCFs.getInheritedCustomField()
									.add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
						}
					}
				}
			}

		} else if (inheritCF != CustomFieldInheritanceEnum.INHERIT_NONE) {
			ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
			if (parentEntities != null) {
				for (ICustomFieldEntity parentEntity : parentEntities) {
					if (parentEntity == null) {
						continue;
					}
					if (parentEntity instanceof Provider && ((Provider) parentEntity).getCode() == null) {
						parentEntity = appProvider;
					}
					// logger.trace("Parent entity: {}", parentEntity);

					// Append inherited values only
					if (accumulateCF) {
						if (parentEntity.getCfAccumulatedValues() != null) {
							for (Entry<String, List<CustomFieldValue>> cfValueInfo : parentEntity
									.getCfAccumulatedValues().getValuesByCode().entrySet()) {
								String cfCode = cfValueInfo.getKey();
								// Return only those values that have cft
								if (!cfts.containsKey(cfCode)) {
									continue;
								}
								for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
									if (!cfValue.isExcessiveInSize()) {
										currentEntityCFs.getInheritedCustomField()
												.add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
									}
								}
							}
						}
					} else {

						// inherit the parent entity's custom fields
						// the current entity's inherited fields are empty so just add all parent CFs
						// directly
						CustomFieldsDto parentCFs = getCustomFieldsDTO(parentEntity, null, inheritCF);
						if (parentCFs != null) {
							// only add the parent CFs to the current entity's inherited custom fields if
							// the current
							// entity's CFTs match with the parent's CF code
							for (CustomFieldDto parentCF : parentCFs.getCustomField()) {
								CustomFieldTemplate template = cfts.get(parentCF.getCode());
								if (template != null) {
									currentEntityCFs.getInheritedCustomField().add(parentCF);
								}
							}

							// inherit the parent entity's inherited custom fields
							// we expect at this point that some of the inherited values are overridden so
							// we need to add only
							// the parent entity's inherited CFs that do not exist in the current entity's
							// inherited CFs
							mergeMapValues(parentCFs.getInheritedCustomField(),
									currentEntityCFs.getInheritedCustomField());

							// Takes care of merging inherited values to current values for INHERIT_MERGED
							// type when CF value accumulation is not turned on
							if (inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED) {
								// if merge is needed, we merge parent CF values first
								mergeMapValues(parentCFs.getCustomField(), currentEntityCFs.getCustomField());
								// then merge also with the parent's inherited CFs
								mergeMapValues(parentCFs.getInheritedCustomField(), currentEntityCFs.getCustomField());
							}
						}
					}
				}
			}
		}

		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
	}

	/**
	 * Merge map values.
	 *
	 * @param source      the source
	 * @param destination the destination
	 */
	private void mergeMapValues(List<CustomFieldDto> source, List<CustomFieldDto> destination) {
		for (CustomFieldDto sourceCF : source) {
			// logger.trace("Source custom field: {}", sourceCF);
			boolean found = false;
			// look for a matching CF in the destination
			for (CustomFieldDto destinationCF : destination) {
				// logger.trace("Comparing to destination custom field: {}", destinationCF);
				found = destinationCF.getCode().equalsIgnoreCase(sourceCF.getCode());
				if (found) {
					// logger.trace("Custom field matched: \n{}\n{}", sourceCF, destinationCF);
					Map<String, CustomFieldValueDto> sourceValues = sourceCF.getMapValue();
					if (sourceValues != null) {
						Map<String, CustomFieldValueDto> destinationValues = destinationCF.getMapValue();
						for (Entry<String, CustomFieldValueDto> sourceValue : sourceValues.entrySet()) {
							CustomFieldValueDto destinationValue = destinationValues.get(sourceValue.getKey());
							// the source value is not allowed to override the destination value, so only
							// add
							// the values that are on the source CF, but not on the destination CF
							if (destinationValue == null) {
								destinationValues.put(sourceValue.getKey(), sourceValue.getValue());
							}
						}
					}
					break;
				}
			}
			// after comparing all CFs, add the source CF that doesn't exist yet in the
			// destination
			if (!found) {
				destination.add(sourceCF);
			}
		}
	}

	private String getFormattedDateValue(Date dateValue, String pattern) {
		String formattedValue = null;
		if (!StringUtils.isBlank(pattern)) {
			SimpleDateFormat simpleDateFormat = null;
			try {
				simpleDateFormat = new SimpleDateFormat(pattern);
			} catch (IllegalArgumentException e) {
				simpleDateFormat = new SimpleDateFormat();
			}
			formattedValue = simpleDateFormat.format(dateValue);
		}
		return formattedValue;
	}

	private String getFormattedDecimalValue(Number numberValue, String pattern) {
		String formattedValue = null;
		if (!StringUtils.isBlank(pattern)) {
			DecimalFormat decimalFormat = null;
			try {
				decimalFormat = new DecimalFormat(pattern);
			} catch (IllegalArgumentException e) {
				decimalFormat = new DecimalFormat();
			}
			formattedValue = decimalFormat.format(numberValue);
		}
		return formattedValue;
	}

	private String getFormattedValue(CustomFieldTemplate cft, Object value) {
		String formattedValue = null;
		if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
			formattedValue = getFormattedDecimalValue((Long) value, cft.getDisplayFormat());
		}
		if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
			formattedValue = getFormattedDecimalValue((Double) value, cft.getDisplayFormat());
		}
		if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
			formattedValue = getFormattedDateValue((Date) value, cft.getDisplayFormat());
		}
		return formattedValue;
	}

	private void setFormattedDateValue(CustomFieldDto dto, CustomFieldTemplate cft) {
		if (!StringUtils.isBlank(cft.getDisplayFormat()) && cft.getFieldType() == CustomFieldTypeEnum.DATE) {
			CustomFieldFormattedValueDto formattedValueDto = new CustomFieldFormattedValueDto();
			formattedValueDto.setSingleValue(getFormattedDateValue(dto.getDateValue(), cft.getDisplayFormat()));
			dto.setFormattedValue(formattedValueDto);
		}
	}

	private void setFormattedDecimalValue(CustomFieldDto dto, CustomFieldTemplate cft, Number numberValue) {
		if (!StringUtils.isBlank(cft.getDisplayFormat()) && (cft.getFieldType() == CustomFieldTypeEnum.LONG
				|| cft.getFieldType() == CustomFieldTypeEnum.DOUBLE)) {
			CustomFieldFormattedValueDto formattedValueDto = new CustomFieldFormattedValueDto();
			formattedValueDto.setSingleValue(getFormattedDecimalValue(numberValue, cft.getDisplayFormat()));
			dto.setFormattedValue(formattedValueDto);
		}
	}

	private void setFormattedListValue(CustomFieldDto dto, CustomFieldTemplate cft,
			@SuppressWarnings("rawtypes") List listValue) {
		if (!StringUtils.isBlank(cft.getDisplayFormat())
				&& (cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE
						|| cft.getFieldType() == CustomFieldTypeEnum.DATE)) {
			if (listValue != null && !listValue.isEmpty()) {
				CustomFieldFormattedValueDto formattedValueDto = new CustomFieldFormattedValueDto();
				List<String> listFormattedValue = new ArrayList<String>();
				for (Object item : listValue) {
					listFormattedValue.add(getFormattedValue(cft, item));
				}
				formattedValueDto.setListValue(listFormattedValue);
				dto.setFormattedValue(formattedValueDto);
			}
		}
	}

	private void setFormattedMapValue(CustomFieldDto dto, CustomFieldTemplate cft, Map<String, Object> mapValue) {
		if (!StringUtils.isBlank(cft.getDisplayFormat())
				&& (cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE
						|| cft.getFieldType() == CustomFieldTypeEnum.DATE)) {
			if (mapValue != null && !mapValue.isEmpty()) {
				CustomFieldFormattedValueDto formattedValueDto = new CustomFieldFormattedValueDto();
				LinkedHashMap<String, String> mapFormattedValue = new LinkedHashMap<String, String>();
				for (Map.Entry<String, Object> item : mapValue.entrySet()) {
					mapFormattedValue.put(item.getKey(), getFormattedValue(cft, item.getValue()));
				}
				formattedValueDto.setMapValue(mapFormattedValue);
				dto.setFormattedValue(formattedValueDto);
			}
		}
	}

	/**
	 * Custom field to DTO.
	 *
	 * @param cfCode                 the cf code
	 * @param value                  the value
	 * @param isChildEntityTypeField the is child entity type field
	 * @param cft                    the cft
	 * @return the custom field dto
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CustomFieldDto customFieldToDTO(String cfCode, Object value, boolean isChildEntityTypeField,
			CustomFieldTemplate cft) {

		CustomFieldDto dto = new CustomFieldDto();
		dto.setCode(cfCode);
		dto.setDescription(cft.getDescription());
		dto.setFieldType(cft.getFieldType());
		dto.setLanguageDescriptions(
				LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cft.getDescriptionI18n()));

		if (value instanceof String) {
			dto.setStringValue((String) value);
		} else if (value instanceof Date) {
			dto.setDateValue((Date) value);
			setFormattedDateValue(dto, cft);
		} else if (value instanceof Long) {
			dto.setLongValue((Long) value);
			setFormattedDecimalValue(dto, cft, (Long) value);
		} else if (value instanceof Double) {
			dto.setDoubleValue((Double) value);
			setFormattedDecimalValue(dto, cft, (Double) value);
		} else if (value instanceof List) {
			dto.setListValue(customFieldValueToDTO((List) value, isChildEntityTypeField));
			setFormattedListValue(dto, cft, (List) value);
		} else if (value instanceof Map) {
			dto.setMapValue(customFieldValueToDTO((Map) value));
			setFormattedMapValue(dto, cft, (Map) value);
		} else if (value instanceof EntityReferenceWrapper) {
			dto.setEntityReferenceValue(new EntityReferenceDto((EntityReferenceWrapper) value));
		}

		return dto;
	}

	/**
	 * Custom field to DTO.
	 *
	 * @param cfCode  the cf code
	 * @param cfValue the cf value
	 * @param cft     the cft
	 * @return the custom field dto
	 */
	@SuppressWarnings("unchecked")
	private CustomFieldDto customFieldToDTO(String cfCode, CustomFieldValue cfValue, CustomFieldTemplate cft) {

		boolean isChildEntityTypeField = cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY;

		CustomFieldDto dto = customFieldToDTO(cfCode, cfValue.getValue(), isChildEntityTypeField, cft);
		dto.setCode(cfCode);
		dto.setDescription(cft.getDescription());
		dto.setFieldType(cft.getFieldType());
		dto.setLanguageDescriptions(
				LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cft.getDescriptionI18n()));
		if (cfValue.getPeriod() != null) {
			dto.setValuePeriodStartDate(cfValue.getPeriod().getFrom());
			dto.setValuePeriodEndDate(cfValue.getPeriod().getTo());
		}

		if (cfValue.getPriority() > 0) {
			dto.setValuePeriodPriority(cfValue.getPriority());
		}
		dto.setStringValue(cfValue.getStringValue());
		dto.setDateValue(cfValue.getDateValue());
		dto.setLongValue(cfValue.getLongValue());
		dto.setDoubleValue(cfValue.getDoubleValue());
		dto.setListValue(customFieldValueToDTO(cfValue.getListValue(), isChildEntityTypeField));
		dto.setMapValue(customFieldValueToDTO(cfValue.getMapValue()));

		if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && dto.getMapValue() != null
				&& !dto.getMapValue().isEmpty() && !dto.getMapValue().containsKey(CustomFieldValue.MAP_KEY)) {
			dto.getMapValue().put(CustomFieldValue.MAP_KEY, new CustomFieldValueDto(StringUtils
					.concatenate(CustomFieldValue.MATRIX_COLUMN_NAME_SEPARATOR, cft.getMatrixColumnCodes())));
		}

		if (cfValue.getEntityReferenceValue() != null) {
			dto.setEntityReferenceValue(new EntityReferenceDto(cfValue.getEntityReferenceValue()));
		}

		return dto;
	}

	/**
	 * Custom field value to DTO.
	 *
	 * @param listValue              the list value
	 * @param isChildEntityTypeField the is child entity type field
	 * @return the list
	 */
	private List<CustomFieldValueDto> customFieldValueToDTO(@SuppressWarnings("rawtypes") List listValue,
			boolean isChildEntityTypeField) {

		if (listValue == null) {
			return null;
		}

		List<CustomFieldValueDto> dtos = new ArrayList<CustomFieldValueDto>();

		for (Object listItem : listValue) {
			CustomFieldValueDto dto = new CustomFieldValueDto();
			if (listItem instanceof EntityReferenceWrapper) {
				if (isChildEntityTypeField) {
					// keep query params in dto ==> used in one query later in
					// loadDtosCustomEntityInstances method
					dto.setSearchClassnameCode(((EntityReferenceWrapper) listItem).getClassnameCode());
					dto.setSearchCode(((EntityReferenceWrapper) listItem).getCode());
				} else {
					dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) listItem));
				}
			} else {
				dto.setValue(listItem);
			}
			dtos.add(dto);
		}

		loadDtosCustomEntityInstances(dtos);

		return dtos;
	}
	
	/**
	 * load CustomFieldValueDto dtos and custom entity Instances
	 * 
	 * @param dtos
	 */
	private void loadDtosCustomEntityInstances(List<CustomFieldValueDto> dtos) {

		List<CustomFieldValueDto> dtosWithCustomEntityInstances = dtos.stream()
				.filter(dto -> dto.getSearchCode() != null || dto.getSearchClassnameCode() != null)
				.collect(Collectors.toList());

		if (dtosWithCustomEntityInstances != null && !dtosWithCustomEntityInstances.isEmpty()) {

			List<CustomEntityInstance> ceis = customEntityInstanceService
					.findByCodeByCet(dtosWithCustomEntityInstances);
			if (ceis != null && !ceis.isEmpty()) {

				AppliesToValuesCalculator appliesToValuesCalculator = new AppliesToValuesCalculator();
				appliesToValuesCalculator.calculateCustomEntityInstancesAtvs(ceis);

				Map<String, CustomFieldTemplate> allCfts = null;
				if (appliesToValuesCalculator != null && appliesToValuesCalculator.getAllAtvs() != null
						&& !appliesToValuesCalculator.getAllAtvs().isEmpty()) {
					allCfts = findByAppliesTo(appliesToValuesCalculator.getAllAtvs());
				}

				if (allCfts != null && !allCfts.isEmpty() && ceis != null && !ceis.isEmpty()) {

					for (CustomFieldValueDto dto : dtosWithCustomEntityInstances) {
						for (CustomEntityInstance cei : ceis) {
							if (dto.getSearchClassnameCode() != null && dto.getSearchCode() != null) {
								if (cei.getCetCode() != null && cei.getCode() != null) {
									if (dto.getSearchClassnameCode().equals(cei.getCetCode())
											&& dto.getSearchCode().equals(cei.getCode())) {
										try {
											dto.setValue(new CustomEntityInstanceDto(cei, getCustomFieldsDTO(CustomFieldInheritanceEnum.INHERIT_NONE, allCfts, cei)));
										} catch (CustomFieldException e) {
											logger.error(e.getLocalizedMessage(), e);
										}
									}
								}
							}
						}
					}

					dtos.removeAll(dtos.stream()
							.filter(dto -> dto.getSearchCode() != null || dto.getSearchClassnameCode() != null)
							.collect(Collectors.toList()));

					dtos.addAll(dtosWithCustomEntityInstances);

				}

			}

		}
	}

	/**
	 * Custom field value to DTO.
	 *
	 * @param mapValue the map value
	 * @return the linked hash map
	 */
	private LinkedHashMap<String, CustomFieldValueDto> customFieldValueToDTO(Map<String, Object> mapValue) {
		if (mapValue == null || mapValue.entrySet().size() == 0) {
			return null;
		}
		LinkedHashMap<String, CustomFieldValueDto> dtos = new LinkedHashMap<String, CustomFieldValueDto>();

		for (Map.Entry<String, Object> mapItem : mapValue.entrySet()) {
			CustomFieldValueDto dto = new CustomFieldValueDto();
			if (mapItem.getValue() instanceof EntityReferenceWrapper) {
				dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) mapItem.getValue()));
			} else {
				dto.setValue(mapItem.getValue());
			}
			dtos.put(mapItem.getKey(), dto);
		}
		return dtos;
	}

	/**
	 * find CustomFieldTemplate By AppliesTo values list
	 * 
	 * @param allAtvs
	 * @return
	 */
	public Map<String, CustomFieldTemplate> findByAppliesTo(Set<String> allAtvs) {
		return customFieldTemplateService.findByAppliesTo(allAtvs);
	}

	public CustomFieldsDto getCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			Map<String, CustomFieldTemplate> allCfts, BusinessCFEntity cfEntity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (cfEntity != null && cfEntity.getCftAppliesTo() != null) {

			for (Map.Entry<String, CustomFieldTemplate> entry : allCfts.entrySet()) {

				CustomFieldTemplate cft = entry.getValue();

				if (cft != null && cft.getAppliesTo() != null) {

					if (cfEntity.getCftAppliesTo().equals(cft.getAppliesTo())) {

						Map<String, List<CustomFieldValue>> cfValuesByCode = cfEntity.getCfValues() != null
								? cfEntity.getCfValues().getValuesByCode()
								: new HashMap<>();

						addCustomFieldsDto(inheritCF, allCfts, currentEntityCFs, cfValuesByCode);

						boolean mergeMapValues = inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED;
						boolean includeInheritedCF = mergeMapValues
								|| inheritCF == CustomFieldInheritanceEnum.INHERIT_NO_MERGE;

						// add parent cf values if inherited
						if (includeInheritedCF) {
							ICustomFieldEntity[] parentEntities = cfEntity.getParentCFEntities();
							includeInheritedCustomFields(parentEntities, inheritCF, currentEntityCFs, allCfts,
									mergeMapValues);
						}
						break;
					}
				}
			}
		}
		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
	}

	/**
	 * add CustomFieldsDto to currentEntityCFs  
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param currentEntityCFs
	 * @param cfValuesByCode
	 */
	private void addCustomFieldsDto(CustomFieldInheritanceEnum inheritCF, Map<String, CustomFieldTemplate> cfts,
			CustomFieldsDto currentEntityCFs, Map<String, List<CustomFieldValue>> cfValuesByCode) {

		boolean isValueMapEmpty = cfValuesByCode == null || cfValuesByCode.isEmpty();

		if (!isValueMapEmpty) {

			for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValuesByCode.entrySet()) {

				String cfCode = cfValueInfo.getKey();

				// Return only those values that have cft
				if (!cfts.containsKey(cfCode)) {
					continue;
				}

				for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
					currentEntityCFs.getCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
				}

			}
		}

	}

	/**
	 * includes Inherited Custom Fields
	 * 
	 * @param parentEntities
	 * @param inheritCF
	 * @param currentEntityCFs
	 * @param cfts
	 * @param mergeMapValues
	 * @throws CustomFieldException
	 */
	private void includeInheritedCustomFields(ICustomFieldEntity[] parentEntities, CustomFieldInheritanceEnum inheritCF,
			CustomFieldsDto currentEntityCFs, Map<String, CustomFieldTemplate> cfts, boolean mergeMapValues)
			throws CustomFieldException {

		if (parentEntities != null) {

			Set<String> peDistinctAtvs = new HashSet<String>();

			keepAppliesToValue(parentEntities, peDistinctAtvs);

			if (peDistinctAtvs.size() > 0) {

				Map<String, CustomFieldTemplate> pCfts = customFieldTemplateService.findByAppliesTo(peDistinctAtvs);

				for (ICustomFieldEntity parentEntity : parentEntities) {

					if (parentEntity instanceof Provider && ((Provider) parentEntity).getCode() == null) {
						parentEntity = appProvider;
					}

					CustomFieldsDto parentCFs = getCustomFieldsDTO(inheritCF, pCfts, parentEntity);
					if (parentCFs != null) {

						for (CustomFieldDto parentCF : parentCFs.getCustomField()) {
							CustomFieldTemplate template = cfts.get(parentCF.getCode());
							if (template != null) {
								currentEntityCFs.getInheritedCustomField().add(parentCF);
							}
						}

						mergeMapValues(parentCFs.getInheritedCustomField(), currentEntityCFs.getInheritedCustomField());

						if (mergeMapValues) {
							mergeMapValues(parentCFs.getCustomField(), currentEntityCFs.getCustomField());
							mergeMapValues(parentCFs.getInheritedCustomField(), currentEntityCFs.getCustomField());
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * keeps the used applies_to that was used in customfieldtemplate query
	 * 
	 * @param parentEntities
	 * @param peDistinctAtvs
	 * @throws CustomFieldException
	 */
	private void keepAppliesToValue(ICustomFieldEntity[] parentEntities, Set<String> peDistinctAtvs)
			throws CustomFieldException {
		for (ICustomFieldEntity pentity : parentEntities) {

			String peAppliesToValue = CustomFieldTemplateService.calculateAppliesToValue(pentity);
			if (peAppliesToValue != null) {
				peDistinctAtvs.add(peAppliesToValue);
			}

			if (pentity instanceof BusinessCFEntity) {
				BusinessCFEntity businessCFEntity = (BusinessCFEntity) pentity;
				businessCFEntity.setCftAppliesTo(peAppliesToValue);
				pentity = (ICustomFieldEntity) businessCFEntity;
			} else if (pentity instanceof Access) {
				Access access = (Access) pentity;
				access.setCftAppliesTo(peAppliesToValue);
				pentity = (ICustomFieldEntity) access;
			} else if (pentity instanceof Provider) {
				if (appProvider != null) {
					appProvider.setCftAppliesTo(peAppliesToValue);
					pentity = (ICustomFieldEntity) appProvider;
				} else {
					Provider provider = (Provider) pentity;
					provider.setCftAppliesTo(peAppliesToValue);
					pentity = (ICustomFieldEntity) provider;
				}
			}
		}
	}

	/**
	 * gets CustomFieldsDTO
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param entity
	 * @return
	 * @throws CustomFieldException
	 */
	public CustomFieldsDto getCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			Map<String, CustomFieldTemplate> cfts, ICustomFieldEntity entity) throws CustomFieldException {
		if (entity instanceof BusinessCFEntity) {
			return getCustomFieldsDTO(inheritCF, cfts, (BusinessCFEntity) entity);
		} else if (entity instanceof Access) {
			return getAccessCustomFieldsDTO(inheritCF, cfts, (Access) entity);
		} else if (entity instanceof Provider) {
			return getProviderCustomFieldsDTO(inheritCF, cfts, (Provider) entity);
		}
		return new CustomFieldsDto();
	}

	private CustomFieldsDto getProviderCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			Map<String, CustomFieldTemplate> cfts, Provider entity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (entity != null && entity.getCftAppliesTo() != null) {

			for (Map.Entry<String, CustomFieldTemplate> entry : cfts.entrySet()) {

				CustomFieldTemplate cft = entry.getValue();

				if (entity.getCftAppliesTo().equals(cft.getAppliesTo())) {

					Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues() != null
							? entity.getCfValues().getValuesByCode()
							: new HashMap<>();

					addCustomFieldsDto(inheritCF, cfts, currentEntityCFs, cfValuesByCode);

					boolean mergeMapValues = inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED;
					boolean includeInheritedCF = mergeMapValues
							|| inheritCF == CustomFieldInheritanceEnum.INHERIT_NO_MERGE;

					// add parent cf values if inherited
					if (includeInheritedCF) {
						ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
						includeInheritedCustomFields(parentEntities, inheritCF, currentEntityCFs, cfts, mergeMapValues);
					}
					break;
				}
			}
		}
		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;

	}

	/**
	 * Calculates Access point CustomFieldsDto 
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param entity
	 * @return
	 * @throws CustomFieldException
	 */
	public CustomFieldsDto getAccessCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			Map<String, CustomFieldTemplate> cfts, Access entity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (entity != null && entity.getCftAppliesTo() != null) {

			for (Map.Entry<String, CustomFieldTemplate> entry : cfts.entrySet()) {

				CustomFieldTemplate cft = entry.getValue();

				if (entity.getCftAppliesTo().equals(cft.getAppliesTo())) {

					Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues() != null
							? entity.getCfValues().getValuesByCode()
							: new HashMap<>();

					addCustomFieldsDto(inheritCF, cfts, currentEntityCFs, cfValuesByCode);

					boolean mergeMapValues = inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED;
					boolean includeInheritedCF = mergeMapValues
							|| inheritCF == CustomFieldInheritanceEnum.INHERIT_NO_MERGE;

					// add parent cf values if inherited
					if (includeInheritedCF) {
						ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
						includeInheritedCustomFields(parentEntities, inheritCF, currentEntityCFs, cfts, mergeMapValues);
					}
					break;
				}
			}
		}
		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;

	}

}
