package org.meveo.service.api;

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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
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
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.AppliesToValuesCalculator;
import org.slf4j.Logger;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 5.0.7
 */
@Stateless
public class EntityToDtoConverter {

	@Inject
	private Logger logger;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private CustomEntityInstanceService customEntityInstanceService;

	@Inject
	protected CustomFieldInstanceService customFieldInstanceService;

	@Inject
	@ApplicationProvider
	protected Provider appProvider;

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity) {
		return getCustomFieldsDTO(entity, false);
	}

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, boolean includeInheritedCF) {
		return getCustomFieldsDTO(entity, includeInheritedCF, false);
	}

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, boolean includeInheritedCF,
			boolean mergeMapValues) {
		Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues() != null
				? entity.getCfValues().getValuesByCode()
				: new HashMap<>();
		return getCustomFieldsDTO(entity, cfValuesByCode, includeInheritedCF, mergeMapValues);
	}

	/**
	 * @param entity             entity
	 * @param cfValuesByCode     List of custom field values by code
	 * @param includeInheritedCF If true, also returns the inherited cfs
	 * @param mergeMapValues     If true, merge the map values between instance cf
	 *                           and parent. Use to show a single list of values.
	 * @return custom fields dto
	 */
	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity,
			Map<String, List<CustomFieldValue>> cfValuesByCode, boolean includeInheritedCF, boolean mergeMapValues) {
		if (cfValuesByCode == null) {
			if (includeInheritedCF) {
				cfValuesByCode = new HashMap<>();
			} else {
				return null;
			}
		}
		return getCustomFieldsDTO(entity, cfValuesByCode,
				CustomFieldInheritanceEnum.getInheritCF(includeInheritedCF, mergeMapValues));
	}

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, CustomFieldInheritanceEnum inheritCF) {
		Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues() != null
				? entity.getCfValues().getValuesByCode()
				: new HashMap<>();
		return getCustomFieldsDTO(entity, cfValuesByCode, inheritCF);
	}

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldValue>> cfValues,
			CustomFieldInheritanceEnum inheritCF) {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();
		boolean isValueMapEmpty = cfValues == null || cfValues.isEmpty();
		boolean mergeMapValues = inheritCF == CustomFieldInheritanceEnum.INHERIT_MERGED;
		boolean includeInheritedCF = mergeMapValues || inheritCF == CustomFieldInheritanceEnum.INHERIT_NO_MERGE;
		Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);

		logger.trace("Get Custom fields for \nEntity: {}/{}\nCustom Field Values: {}",
				entity.getClass().getSimpleName(), ((IEntity) entity).getId(), cfValues);

		if (!isValueMapEmpty) {
			for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValues.entrySet()) {
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

		// add parent cf values if inherited
		if (includeInheritedCF) {
			ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
			if (parentEntities != null) {

				for (ICustomFieldEntity parentEntity : parentEntities) {
					if (parentEntity instanceof Provider && ((Provider) parentEntity).getCode() == null) {
						parentEntity = appProvider;
					}
					// logger.trace("Parent entity: {}", parentEntity);

					// inherit the parent entity's custom fields
					// the current entity's inherited fields are empty so just add all parent CFs
					// directly
					CustomFieldsDto parentCFs = getCustomFieldsDTO(parentEntity, inheritCF);
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
						mergeMapValues(parentCFs.getInheritedCustomField(), currentEntityCFs.getInheritedCustomField());

						if (mergeMapValues) {
							// if merge is needed, we merge parent CF values first
							mergeMapValues(parentCFs.getCustomField(), currentEntityCFs.getCustomField());
							// then merge also with the parent's inherited CFs
							mergeMapValues(parentCFs.getInheritedCustomField(), currentEntityCFs.getCustomField());
						}
					}
				}
			}
		}
		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
	}

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CustomFieldDto customFieldToDTO(String cfCode, Object value, boolean isChildEntityTypeField) {

		CustomFieldDto dto = new CustomFieldDto();
		dto.setCode(cfCode);
		if (value instanceof String) {
			dto.setStringValue((String) value);
		} else if (value instanceof Date) {
			dto.setDateValue((Date) value);
		} else if (value instanceof Long) {
			dto.setLongValue((Long) value);
		} else if (value instanceof Double) {
			dto.setDoubleValue((Double) value);
		} else if (value instanceof List) {
			dto.setListValue(customFieldValueToDTO((List) value, isChildEntityTypeField));
		} else if (value instanceof Map) {
			dto.setMapValue(customFieldValueToDTO((Map) value));
		} else if (value instanceof EntityReferenceWrapper) {
			dto.setEntityReferenceValue(new EntityReferenceDto((EntityReferenceWrapper) value));
		}

		return dto;
	}

	@SuppressWarnings("unchecked")
	private CustomFieldDto customFieldToDTO(String cfCode, CustomFieldValue cfValue, CustomFieldTemplate cft) {

		boolean isChildEntityTypeField = cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY;

		CustomFieldDto dto = customFieldToDTO(cfCode, cfValue.getValue(), isChildEntityTypeField);
		dto.setCode(cfCode);
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

				List<CustomFieldTemplate> allCfts = null;
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
											dto.setValue(CustomEntityInstanceDto.toDTO(cei, getCustomFieldsDTO(
													CustomFieldInheritanceEnum.INHERIT_NONE, allCfts, cei)));
										} catch (CustomFieldException e) {
											dto.setValue(null);
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
	 * Find a list of custom field templates corresponding to a given appliesToValues list 
	 * 
	 * @param appliesToValues
	 * @return Map<String, CustomFieldTemplate>
	 */
	public List<CustomFieldTemplate> findByAppliesTo(Set<String> appliesToValues) {
		return customFieldTemplateService.findByAppliesTo(appliesToValues);
	}

	/**
	 * gets Custom Fields DTO
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param entity
	 * @return CustomFieldsDto
	 * @throws CustomFieldException
	 */
	public CustomFieldsDto getCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			List<CustomFieldTemplate> cfts, ICustomFieldEntity entity) throws CustomFieldException {
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
			List<CustomFieldTemplate> cfts, Provider entity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (entity != null && entity.getCftAppliesTo() != null) {
			
			for(CustomFieldTemplate cft : cfts) {

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
	 * add Custom Fields Dto to currentEntityCFs
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param currentEntityCFs
	 * @param cfValuesByCode
	 */
	private void addCustomFieldsDto(CustomFieldInheritanceEnum inheritCF, List<CustomFieldTemplate> cfts,
			CustomFieldsDto currentEntityCFs, Map<String, List<CustomFieldValue>> cfValuesByCode) {
		boolean isValueMapEmpty = cfValuesByCode == null || cfValuesByCode.isEmpty();
		if (!isValueMapEmpty) {
			for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValuesByCode.entrySet()) {
				String cfCode = cfValueInfo.getKey();
				for (CustomFieldTemplate cft : cfts) {
					if (cft.getCode() != null && cfCode != null && cft.getCode().equalsIgnoreCase(cfCode)) {
						for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
							currentEntityCFs.getCustomField().add(customFieldToDTO(cfCode, cfValue, cft));
						}
						break;
					}
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
			CustomFieldsDto currentEntityCFs, List<CustomFieldTemplate> cfts, boolean mergeMapValues)
			throws CustomFieldException {

		if (parentEntities != null) {

			Set<String> peDistinctAtvs = new HashSet<String>();

			keepAppliesToValue(parentEntities, peDistinctAtvs);

			if (peDistinctAtvs.size() > 0) {

				List<CustomFieldTemplate> pCfts = customFieldTemplateService.findByAppliesTo(peDistinctAtvs);

				for (ICustomFieldEntity parentEntity : parentEntities) {
					if (parentEntity instanceof Provider && ((Provider) parentEntity).getCode() == null) {
						parentEntity = appProvider;
					}

					CustomFieldsDto parentCFs = getCustomFieldsDTO(inheritCF, pCfts, parentEntity);
					if (parentCFs != null) {

						for (CustomFieldDto parentCF : parentCFs.getCustomField()) {
							for(CustomFieldTemplate cft : cfts) {
								if(cft.getCode().equals(parentCF.getCode())) {
									currentEntityCFs.getInheritedCustomField().add(parentCF);
									break;
								}
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
	 * keep Applies To Values in entities (transient fields) 
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
	 * gets Access Point Custom Fields DTO
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param entity
	 * @return
	 * @throws CustomFieldException
	 */
	public CustomFieldsDto getAccessCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			List<CustomFieldTemplate> cfts, Access entity) throws CustomFieldException {
		
		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (entity != null && entity.getCftAppliesTo() != null) {
			
			for(CustomFieldTemplate cft : cfts) {
				
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
	 * gets Custom Fields DTO for BusinessCF Entity 
	 * 
	 * @param inheritCF
	 * @param cfts
	 * @param entity
	 * @return
	 * @throws CustomFieldException
	 */
	public CustomFieldsDto getCustomFieldsDTO(CustomFieldInheritanceEnum inheritCF,
			List<CustomFieldTemplate> cfts, BusinessCFEntity entity) throws CustomFieldException {

		CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

		if (entity != null && entity.getCftAppliesTo() != null) {
			
			for(CustomFieldTemplate cft : cfts) {
				
				if (cft != null && cft.getAppliesTo() != null) {

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
							includeInheritedCustomFields(parentEntities, inheritCF, currentEntityCFs, cfts,
									mergeMapValues);
						}
						break;
					}

				}
				
			}

		}
		return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
	}

}
