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

package org.meveo.service.script.catalog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.rating.EDR;
import org.meveo.service.script.ScriptInstanceService;

/**
 * Takes care of {@link TriggeredEdrScript} related script method invocation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Stateless
public class TriggeredEdrScriptService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public EDR updateEdr(String scriptCode, EDR edr, WalletOperation wo) throws BusinessException {
        TriggeredEdrScriptInterface scriptInterface = (TriggeredEdrScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(TriggeredEdrScript.CONTEXT_ENTITY, edr);
        scriptContext.put(TriggeredEdrScript.CONTEXT_WO, wo);

        return scriptInterface.updateEdr(scriptContext);
    }

}
