package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.commons.utils.StringUtils;

/**
 * Payment by Stripe payment method
 * 
 * @author anasseh
 */
@Entity
@DiscriminatorValue(value = "STRIPE")
public class StripePaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571611074346199L;
    
    public StripePaymentMethod() {
        this.paymentType = PaymentMethodEnum.STRIPE;
    }

    public StripePaymentMethod(boolean isDisabled, String alias, boolean preferred, CustomerAccount customerAccount,String userId) {
        super();
        setDisabled(isDisabled);
        this.paymentType = PaymentMethodEnum.STRIPE;
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
        if(StringUtils.isBlank(userId)) {
        	this.userId = customerAccount.getContactInformationNullSafe().getEmail();
        }else {
        	this.userId = userId;
        }
    }

    public StripePaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.STRIPE;
        this.alias = alias;
        this.preferred = preferred;
    }
    


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof StripePaymentMethod)) {
			return false;
		}

		StripePaymentMethod other = (StripePaymentMethod) obj;

		if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
			return true;
		}

		if (getUserId() != null && getUserId().equals(other.getUserId())) {
			return true;
		}

		
		return false;
	}

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
    }

    @Override
    public String toString() {
        return "STRIPEPaymentMethod [alias= " + getAlias() + ", preferred=" + isPreferred() + "]";
    }
}