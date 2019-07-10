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
package org.meveo.admin.action.finance;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.finance.AccountingWriting;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.AccountingWritingService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Standard backing bean for {@link AccountingWriting} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 *
 * @author mboukayoua
 */
@Named
@ViewScoped
public class AccountingWritingBean extends BaseBean<AccountingWriting> {

    /**
     * Injected @{link {@link AccountingWriting} service. Extends {@link PersistenceService}
     */
    @Inject
    private AccountingWritingService accountingWritingService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public AccountingWritingBean() {
        super(AccountingWriting.class);
    }

    /**
     * Method that returns concrete PersistenceService for an entity class backing bean is bound to.
     * That service is then used for operations on concrete entities (eg. save, delete
     * etc).
     *
     * @return Persistence service
     */
    @Override
    protected IPersistenceService<AccountingWriting> getPersistenceService() {
        return accountingWritingService;
    }
}
