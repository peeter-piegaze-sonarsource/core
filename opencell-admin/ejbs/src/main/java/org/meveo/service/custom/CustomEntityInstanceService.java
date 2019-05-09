package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.base.BusinessService;

/**
 * CustomEntityInstance persistence service implementation.
 * @author unknown
 * @author melyoussoufi
 * 
 */
@Stateless
public class CustomEntityInstanceService extends BusinessService<CustomEntityInstance> {

	public CustomEntityInstance findByCodeByCet(String cetCode, String code) {
		QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
		qb.addCriterion("cei.cetCode", "=", cetCode, true);
		qb.addCriterion("cei.code", "=", code, true);

		try {
			return (CustomEntityInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("No CustomEntityInstance by code {} and cetCode {} found", code, cetCode);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CustomEntityInstance> findChildEntities(String cetCode, String parentEntityUuid) {

		QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
		qb.addCriterion("cei.cetCode", "=", cetCode, true);
		qb.addCriterion("cei.parentEntityUuid", "=", parentEntityUuid, true);

		return qb.getQuery(getEntityManager()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CustomEntityInstance> findByCodeByCet(List<CustomFieldValueDto> dtosTo) {
		final String query = buildFindByCodeByCetNativeQuery(dtosTo);
		if (query != null) {
			return getEntityManager().createNativeQuery(query, CustomEntityInstance.class).getResultList();
		}
		return new ArrayList<CustomEntityInstance>();
	}

	private String buildFindByCodeByCetNativeQuery(List<CustomFieldValueDto> dtosTo) {
		String res = null;
		StringBuilder queryBuilder = new StringBuilder("select * from cust_cei where ");
		if (dtosTo != null && dtosTo.size() > 0) {
			boolean firstTime = true;
			for (CustomFieldValueDto dto : dtosTo) {
				if (!firstTime) {
					queryBuilder.append(" or ");
				}
				queryBuilder.append("(lower(cet_code) = ");
				if (dto.getSearchClassnameCode() != null) {
					queryBuilder.append("'").append(dto.getSearchClassnameCode().toLowerCase()).append("'");
				} else {
					queryBuilder.append("null");
				}
				queryBuilder.append(" and ");
				queryBuilder.append("lower(code) = ");
				if (dto.getSearchCode() != null) {
					queryBuilder.append("'").append(dto.getSearchCode().toLowerCase()).append("'");
				} else {
					queryBuilder.append("null");
				}
				queryBuilder.append(") ");
				firstTime = false;
			}
			res = queryBuilder.toString();
			log.debug("FindByCodeByCetNativeQuery : " + res);
		}
		return res;
	}

}