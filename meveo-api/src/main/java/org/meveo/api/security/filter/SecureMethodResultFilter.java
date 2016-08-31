package org.meveo.api.security.filter;

import javax.inject.Inject;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * Implements filtering logic for a specific DTO.
 *
 * @author Tony Alejandro
 */
public abstract class SecureMethodResultFilter {

	@Inject
	protected Logger log;

	/**
	 * This method returns the class instance. It is used by the
	 * {@link SecureMethodResultFilterFactory} for locating the correct filter.
	 * 
	 * @return The class instance of this filter class.
	 */
	public Class<? extends SecureMethodResultFilter> getFilterClass() {
		return this.getClass();
	}

	/**
	 * This method should check if the result object contains
	 * {@link SecuredEntity} instances and if the user is not authorized to
	 * access these entities, should be filtered out.
	 * 
	 * @param result
	 *            The result object that will be filtered for inaccessible
	 *            entities.
	 * @param user
	 *            The user account that will be used to verify authorization.
	 * @return The filtered result object.
	 */
	public abstract Object filterResult(Object result, User user) throws MeveoApiException;

}
