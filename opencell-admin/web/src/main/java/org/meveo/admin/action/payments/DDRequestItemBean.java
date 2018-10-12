/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.DDRequestItemService;

/**
 * Standard backing bean for {@link DDRequestItem} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class DDRequestItemBean extends BaseBean<DDRequestItem> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DDRequestItem} service. Extends {@link PersistenceService} .
     */
    @Inject
    private DDRequestItemService ddRequestItemService;


    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DDRequestItemBean() {
        super(DDRequestItem.class);
    }

   
    
    

    @Override
    protected IPersistenceService<DDRequestItem> getPersistenceService() {
        return ddRequestItemService;
    }

    public PaginationDataModel<DDRequestItem> getItemsByLot(DDRequestLOT ddRequestLOT) {
        PaginationDataModel<DDRequestItem> items = new PaginationDataModel<DDRequestItem>(ddRequestItemService);
        Map<String, Object> filters2 = new HashMap<String, Object>();
        filters2.put("ddRequestLOT", ddRequestLOT);
        items.addFilters(filters2);
        items.addFetchFields(getListFieldsToFetch());
        items.forceRefresh();
        return items;
    }

}