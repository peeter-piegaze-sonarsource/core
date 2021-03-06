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

package org.meveo.admin.util;

import java.io.Serializable;
import java.text.MessageFormat;

public class ResourceBundle implements Serializable {

    private static final long serialVersionUID = -5269169718061449505L;

    private transient java.util.ResourceBundle proxiedBundle;

    public ResourceBundle(java.util.ResourceBundle proxiedBundle) {
        this.proxiedBundle = proxiedBundle;
    }

    public String getString(String key, Object... params) {
        if (!proxiedBundle.containsKey(key)) {
            return key;
        }
        String msgValue = proxiedBundle.getString(key);
        if (params.length == 0) {
            return msgValue;
        }

        MessageFormat messageFormat = new MessageFormat(msgValue);
        return messageFormat.format(params);
    }
}