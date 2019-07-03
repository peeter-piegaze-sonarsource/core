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
package org.meveo.service.bi.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;

/**
 * Flat file service
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */

@Stateless
public class FlatFileService extends BusinessService<FlatFile> {

    public FlatFile create(String fileName, FileFormat fileFormat, String errorMessage, FileStatusEnum status) throws BusinessException {
        FlatFile flatFile = new FlatFile();
        flatFile.setFileName(fileName);
        flatFile.setFileFormat(fileFormat);
        flatFile.setErrorMessage(errorMessage);
        flatFile.setStatus(status);
        create(flatFile);
        String code = (fileFormat != null ? fileFormat.getCode() : "CODE") + flatFile.getId();
        flatFile.setCode(code);
        return flatFile;
    }

    public void update(FlatFile flatFile, String errorMessage, FileStatusEnum status) throws BusinessException {
        flatFile.setErrorMessage(errorMessage);
        flatFile.setStatus(status);
        update(flatFile);
    }
}