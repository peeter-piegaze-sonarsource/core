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

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateTerminationsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateTerminationsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7148418773670960365L;

    /** The service charge template termination. */
    private List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination;

    /**
     * Gets the service charge template termination.
     *
     * @return the service charge template termination
     */
    public List<ServiceChargeTemplateTerminationDto> getServiceChargeTemplateTermination() {
        if (serviceChargeTemplateTermination == null)
            serviceChargeTemplateTermination = new ArrayList<ServiceChargeTemplateTerminationDto>();
        return serviceChargeTemplateTermination;
    }

    /**
     * Sets the service charge template termination.
     *
     * @param serviceChargeTemplateTermination the new service charge template termination
     */
    public void setServiceChargeTemplateTermination(List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination) {
        this.serviceChargeTemplateTermination = serviceChargeTemplateTermination;
    }

    @Override
    public String toString() {
        return "ServiceChargeTemplateTerminationsDto [serviceChargeTemplateTermination=" + serviceChargeTemplateTermination + "]";
    }

}