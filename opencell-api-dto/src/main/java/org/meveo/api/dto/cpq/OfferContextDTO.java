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

package org.meveo.api.dto.cpq;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "OfferContextDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferContextDTO extends EnableBusinessDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2850157608109341441L;



    /**
     * The billing account code
     */
    private String billingAccountCode;
    /**
     * The selected offer template code in the quote
     */
    private String offerCode;
    
    
    private List<String> sellerWallet;
    /**
     * The select products in the quote
     */
    private List<ProductContextDTO> products;
	public String getBillingAccountCode() {
		return billingAccountCode;
	}
	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}
	public String getOfferCode() {
		return offerCode;
	}
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}
	public List<ProductContextDTO> getProducts() {
		return products;
	}
	public void setProducts(List<ProductContextDTO> products) {
		this.products = products;
	}
	public List<String> getSellerWallet() {
		return sellerWallet;
	}
	public void setSellerWallet(List<String> sellerWallet) {
		this.sellerWallet = sellerWallet;
	}

    
    
    
    
}