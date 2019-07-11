package org.meveo.apiv2.ordering.mappers;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.ordering.order.OrderMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.shared.DateUtils;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class OrderMapperTest implements ResourceMapperTest {
    private OrderItemMapperTest orderItemMapperTest;
    @Before
    public void setUp() {
        orderItemMapperTest = new OrderItemMapperTest();
    }

    @Override
    @Test
    public void mapDtoToEntityAndEntityToDtoTest() {
        OrderMapper orderMapper = new OrderMapper();
        Order baseEntity = (Order) generateEntity();
        org.meveo.apiv2.ordering.order.Order order = orderMapper.toResource(baseEntity);
        Order orderFromResource = orderMapper.toEntity(order);

        assertSame(orderFromResource.getId(), baseEntity.getId());
        assertEquals(orderFromResource.getCode(), baseEntity.getCode());
        assertEquals(orderFromResource.getDescription(), baseEntity.getDescription());
        assertEquals(orderFromResource.getPriority(), baseEntity.getPriority());
        assertEquals(orderFromResource.getCategory(), baseEntity.getCategory());
        assertEquals(orderFromResource.getStatus(), baseEntity.getStatus());
        assertEquals(orderFromResource.getStatusMessage(), baseEntity.getStatusMessage());
        assertEquals(orderFromResource.getOrderDate(), baseEntity.getOrderDate());
        assertEquals(orderFromResource.getRequestedCompletionDate(), baseEntity.getRequestedCompletionDate());

        assertEquals(orderFromResource.getStartDate(), baseEntity.getStartDate());
        assertEquals(orderFromResource.getDeliveryInstructions(), baseEntity.getDeliveryInstructions());
        assertEquals(orderFromResource.getBillingCycle().getId(), baseEntity.getBillingCycle().getId());
        assertEquals(orderFromResource.getPaymentMethod().getPaymentType(), baseEntity.getPaymentMethod().getPaymentType());

        assertSame(orderFromResource.getOrderItems().get(0).getId(), baseEntity.getOrderItems().get(0).getId());
        assertSame(orderFromResource.getOrderItems().get(0).getItemId(), baseEntity.getOrderItems().get(0).getItemId());
        assertSame(orderFromResource.getOrderItems().get(0).getStatus(), baseEntity.getOrderItems().get(0).getStatus());
    }

    @Override
    public BaseEntity generateEntity() {
        Order order = new Order();
        order.setCode("CODE");
        order.setId(123L);
        order.setDescription("Description");
        order.setPriority(1);
        order.setCategory("Category");
        order.setStatus(OrderStatusEnum.valueByApiState("ACKNOWLEDGED"));
        order.setStatusMessage("StatusMessage");
        order.setOrderDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));
        order.setExpectedCompletionDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));
        order.setRequestedCompletionDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));
        order.setStartDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));
        order.setDeliveryInstructions("DeliveryInstructions");

        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setId(234L);
        order.setBillingCycle(billingCycle);

        order.setPaymentMethod(new CardPaymentMethod());
        order.getPaymentMethod().setId(567L);

        order.setOrderItems(Collections.singletonList((OrderItem) orderItemMapperTest.generateEntity()));

        return order;
    }
}
