package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.security.MeveoUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "cpq_attribute_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
public class AttributeInstance extends AttributeValue<AttributeInstance> {

    @ManyToOne
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;
    @OneToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public AttributeInstance() {
    }

    public AttributeInstance(QuoteAttribute quoteAttribute) {
        attribute=quoteAttribute.getAttribute();
        stringValue=quoteAttribute.getStringValue();
        dateValue=quoteAttribute.getDateValue();
        doubleValue=quoteAttribute.getDoubleValue();
        assignedAttributeValue = quoteAttribute.getAssignedAttributeValue()
                                        .stream()
                                        .map(AttributeInstance::new)
                                        .collect(Collectors.toList());
    }

    public AttributeInstance(OrderAttribute orderAttribute, MeveoUser currentUser) {
        attribute=orderAttribute.getAttribute();
        stringValue=orderAttribute.getStringValue();
        dateValue=orderAttribute.getDateValue();
        doubleValue=orderAttribute.getDoubleValue();
        updateAudit(currentUser);
        assignedAttributeValue = orderAttribute.getAssignedAttributeValue()
                .stream()
                .map(oa -> {
                    AttributeInstance attributeInstance = new AttributeInstance(oa, currentUser);
                    attributeInstance.setParentAttributeValue(this);
                    return attributeInstance;
                })
                .collect(Collectors.toList());
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeInstance)) return false;
        if (!super.equals(o)) return false;
        AttributeInstance that = (AttributeInstance) o;
        return Objects.equals(serviceInstance, that.serviceInstance) &&
                Objects.equals(subscription, that.subscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceInstance, subscription);
    }
}