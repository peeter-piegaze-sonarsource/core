package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstancesDto implements Serializable {

	private static final long serialVersionUID = -3365845607399516795L;

	private List<ProductInstanceDto> productInstances = new ArrayList<ProductInstanceDto>();

	public List<ProductInstanceDto> getProductInstances() {
		return productInstances;
	}

	public void setProductInstances(List<ProductInstanceDto> productInstances) {
		this.productInstances = productInstances;
	}

}
