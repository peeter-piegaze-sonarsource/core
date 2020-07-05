/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationActionEnum;
import org.meveo.model.billing.WalletOperationAggregationLine;
import org.meveo.model.billing.WalletOperationAggregationMatrix;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.filter.FilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Generates the query use to group wallet operations with the given
 * {@link RatedTransactionsJobAggregationSetting}.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class WalletOperationAggregatorQueryBuilder {
	private Logger log = LoggerFactory.getLogger(WalletOperationAggregatorQueryBuilder.class);

	private RatedTransactionsJobAggregationSetting aggregationSettings;
	private String groupBy = "";
	private String id = "";
	private String dateAggregateSelect = "";
	private String select = "";
	private String where = "";

	private CustomFieldTemplateService customFieldTemplateService;
	private FilterService filterService;

	/**
	 * @param aggregationSettings the aggregation settings matrix
	 */
	public WalletOperationAggregatorQueryBuilder(RatedTransactionsJobAggregationSetting aggregationSettings, CustomFieldTemplateService customFieldTemplateService,
			FilterService filterService) {
		this.aggregationSettings = aggregationSettings;
		this.customFieldTemplateService = customFieldTemplateService;
		this.filterService = filterService;
		prepareQuery();
	}

	private void prepareQuery() {
		WalletOperationAggregationMatrix aggregationMatrix = aggregationSettings.getWalletOperationAggregationMatrix();
		List<WalletOperationAggregationLine> aggregationLines = aggregationMatrix.getAggregationLines();
		for (WalletOperationAggregationLine aggregationLine : aggregationLines) {
			select += getSelect(aggregationLine);
			groupBy += getQueryGroupBy(aggregationLine);
		}
		if (aggregationSettings.isPeriodAggregation()) {
			groupBy += "op.period, ";
		}
		if (!StringUtils.isBlank(select)) {
			select = select.substring(0, select.length() - 2);
		}
		if (!StringUtils.isBlank(groupBy)) {
			groupBy = groupBy.substring(0, groupBy.length() - 2);
		}
		if (aggregationSettings.getFilter() != null) {
			where = getWhere(aggregationSettings.getFilter());
		}

	}

	private String getWhere(Filter filter) {
		filter = filterService.refreshOrRetrieve(filter);
		FilteredQueryBuilder filteredQueryBuilder = new FilteredQueryBuilder(filter);
		String query = filteredQueryBuilder.getSqlString();
		if (query != null && query.contains("where")) {
			String[] parts = query.toLowerCase().split("where");
			if (!parts[1].contains("order")) {
				return " AND (" + parts[1] + ")";
			} else {
				String[] wheres = parts[1].split("order");
				return "AND (" + wheres[0] + ")";
			}
		}
		return "";
	}

	private String getQueryGroupBy(WalletOperationAggregationLine aggregationLine) {
		String field = aggregationLine.getField();

		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.TRUNCATE)) {
			if (aggregationLine.getValue().equalsIgnoreCase("day")) {
				return "YEAR(op." + field + "), MONTH(op." + field + "), DAY(op." + field + "), ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("month")) {
				return "YEAR(op." + field + "), MONTH(op." + field + "), ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("year")) {
				return "YEAR(op." + field + "), ";
			}
		}
		if (aggregationLine.getGroupBy() != null) {
			if (aggregationLine.getGroupByParameter() != null) {
				return aggregationLine.getGroupBy() + "(op." + field + "," + aggregationLine.getGroupByParameter() + "), ";
			} else {
				return aggregationLine.getGroupBy() + "(op." + field + "), ";
			}
		}
		if (aggregationLine.isCustomField()) {
			return getCustomFieldGroupBy(aggregationLine);
		}
		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.KEY)) {
			return getFieldGroupBy(field);
		}
		return "";
	}

	private String getFieldGroupBy(String fieldName) {
		try {
			boolean isEntity = false;
			if (!fieldName.contains(".")) {
				Field field = WalletOperation.class.getDeclaredField(fieldName);
				Class classField = field.getType();
				isEntity = BaseEntity.class.isAssignableFrom(classField);
			}
			if (fieldName.contains(".") || isEntity) {
				return "op." + fieldName + ".id, ";
			}
			return "op." + fieldName + ", ";
		} catch (NoSuchFieldException e) {
			log.warn("No such field {} exist in WalletOperation Class", fieldName);
			return "";
		}

	}

	private String getCustomFieldGroupBy(WalletOperationAggregationLine aggregationLine) {
		String field = aggregationLine.getField();
		CustomFieldTemplate cf = customFieldTemplateService.findByCode(field);
		if (cf == null) {
			throw new BusinessException("Custom field '" + field + "' not found");
		}
		if (aggregationLine.getGroupBy() != null) {
			if (aggregationLine.getGroupByParameter() != null) {
				return aggregationLine.getGroupBy() + "(varcharFromJson(op.cfValues, " + field + ", " + cf.getFieldType().name().toLowerCase() + "), " + aggregationLine
						.getGroupByParameter() + "), ";
			} else {
				return aggregationLine.getGroupBy() + "(varcharFromJson(op.cfValues, " + field + ", " + cf.getFieldType().name().toLowerCase() + ")), ";
			}
		} else {
			return "varcharFromJson(op.cfValues, " + field + ", " + cf.getFieldType().name().toLowerCase() + "), ";
		}
	}

	private String getSelect(WalletOperationAggregationLine aggregationLine) {
		String selectStr = "";
		String field = aggregationLine.getField();
		String alias = aggregationLine.getAlias();
		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.EMPTY)) {
			selectStr = "";
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.KEY)) {
			if (aggregationLine.isCustomField()) {
				selectStr = getCustomFieldSelect(field, alias);
			} else {
				selectStr = getSelectField(field, alias);
			}
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.TRUNCATE)) {
			if (aggregationLine.getValue().equalsIgnoreCase("day")) {
				selectStr = "YEAR(op." + field + ") as year, MONTH(op." + field + ") as month, DAY(op." + field + ") as day ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("month")) {
				selectStr = "YEAR(op." + field + ") as year, MONTH(op." + field + ") as month, 1 as day ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("year")) {
				selectStr = "YEAR(op." + field + "), as year, 0 as month, 1 as day ";
			}
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.VALUE)) {
			selectStr = "'" + aggregationLine.getValue() + "' as " + field;
		} else {
			selectStr = aggregationLine.getAction() + "(op." + field + ") as " + field;
		}
		if (StringUtils.isBlank(selectStr)) {
			return "";
		} else {
			return selectStr + ", ";
		}
	}

	private String getSelectField(String fieldName, String alias) {
		try {
			boolean isEntity = false;
			if (!fieldName.contains(".")) {
				Field field = WalletOperation.class.getDeclaredField(fieldName);
				Class classField = field.getType();
				isEntity = BaseEntity.class.isAssignableFrom(classField);
			}
			if (fieldName.contains(".") || isEntity) {
				return "op." + fieldName + ".id as " + getFieldSuffix(fieldName, alias);
			}
			return "op." + fieldName + " as " + getFieldSuffix(fieldName, alias);
		} catch (NoSuchFieldException e) {
			log.warn("No such field {} exist in WalletOperation Class", fieldName);
			return "";
		}

	}

	private String getCustomFieldSelect(String field, String alias) {
		CustomFieldTemplate cf = customFieldTemplateService.findByCode(field);
		String as = (StringUtils.isBlank(alias)) ? field : alias;
		if (cf == null) {
			throw new BusinessException("Custom field '" + field + "' not found");
		}
		return "varcharFromJson(op.cfValues, " + field + ", " + cf.getFieldType().name().toLowerCase() + ") as " + as;
	}

	private String getFieldSuffix(String field, String alias) {
		if (!StringUtils.isBlank(alias)) {
			return alias;
		}
		if (field.contains(".")) {
			String[] parts = field.split("\\.");
			return parts[parts.length - 1];
		} else {
			return field;
		}
	}

	public String getGroupQuery() {
		return "SELECT " //
				+ "STRING_AGG(cast(op.id as string), ',') as id, " //
				+ select //
				+ " FROM WalletOperationPeriod op " //
				+ " WHERE (op.invoicingDate is NULL or op.invoicingDate<:invoicingDate) " + where //
				+ " GROUP BY " + groupBy;

		//return "select varcharFromJson(cfValues, cf_wo_option_tarifaire_fournisseur, string, string) as result from WalletOperation";

	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSelect() {
		return select;
	}

}
