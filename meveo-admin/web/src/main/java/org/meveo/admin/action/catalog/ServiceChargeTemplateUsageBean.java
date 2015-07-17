/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;


@Named
@ViewScoped
public class ServiceChargeTemplateUsageBean extends BaseBean<ServiceChargeTemplateUsage> {

    private static final long serialVersionUID = 1L;

   
    @Inject
    private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ServiceChargeTemplateUsageBean() {
        super(ServiceChargeTemplateUsage.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<ServiceChargeTemplateUsage> getPersistenceService() {
        return serviceChargeTemplateUsageService;
    }
    public LazyDataModel<ServiceChargeTemplateUsage> getUsageCharges(ServiceTemplate serviceTemplate) {
		if (serviceTemplate != null&&!serviceTemplate.isTransient()) {
			filters.put("serviceTemplate", serviceTemplate);
			return getLazyDataModel();
		}
		return new LazyDataModel<ServiceChargeTemplateUsage>() {
			private static final long serialVersionUID = 1L;
			@Override
			public List<ServiceChargeTemplateUsage> load(int first, int pageSize, String sortField,
					SortOrder sortOrder, Map<String, Object> loadingFilters) {
				return new ArrayList<ServiceChargeTemplateUsage>();
			}
		};
	}

}