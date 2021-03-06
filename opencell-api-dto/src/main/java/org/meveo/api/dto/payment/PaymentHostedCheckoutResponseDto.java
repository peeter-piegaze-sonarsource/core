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

package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;

/**
 * The Class PaymentGatewayResponseDto.
 *
 * @author Mounir Bahije
 */

@XmlRootElement(name = "PaymentHostedCheckoutResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHostedCheckoutResponseDto extends ActionStatus {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3151651854190686940L;

    /** The urlPaymentHostedCheckout. */
    private Result result;

    /**
     * Instantiates a new payment gateway response dto.
     */
    public PaymentHostedCheckoutResponseDto() {

    }

    public PaymentHostedCheckoutResponseDto(String hostedCheckoutUrl, String ca, String returnUrl) {
        Result result = new Result(hostedCheckoutUrl, ca, returnUrl);
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

class Result {
    String hostedCheckoutUrl;
    String ca;
    String returnUrl;

    public Result(String hostedCheckoutUrl, String ca, String returnUrl) {
        this.hostedCheckoutUrl = hostedCheckoutUrl;
        this.ca = ca;
        this.returnUrl = returnUrl;
    }

    public String getHostedCheckoutUrl() {
        return hostedCheckoutUrl;
    }

    public void setHostedCheckoutUrl(String hostedCheckoutUrl) {
        this.hostedCheckoutUrl = hostedCheckoutUrl;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
