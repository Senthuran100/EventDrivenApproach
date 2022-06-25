package com.demo.stream.Orders.model;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface OrderBinder {
    String INVENTORY_CHECK_IN = "inventoryChecking-in";
    String INVENTORY_CHECK_OUT = "inventoryChecking-out";

    @Input(INVENTORY_CHECK_IN)
    SubscribableChannel inventoryCheckingIn();

    @Output(INVENTORY_CHECK_OUT)
    MessageChannel inventoryCheckingOut();

    String ORDER_DLQ = "order-dlq";

    @Input(ORDER_DLQ)
    SubscribableChannel orderIn();

    String SHIPPING_IN = "shipping-in";
    String SHIPPING_OUT = "shipping-out";

    @Input(SHIPPING_IN)
    SubscribableChannel shippingIn();

    @Output(SHIPPING_OUT)
    MessageChannel shippingOut();
}
