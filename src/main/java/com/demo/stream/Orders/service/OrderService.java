package com.demo.stream.Orders.service;

import com.demo.stream.Orders.exception.OrderFailedException;
import com.demo.stream.Orders.exception.OrderNotFoundException;
import com.demo.stream.Orders.model.Order;
import com.demo.stream.Orders.model.OrderBinder;
import com.demo.stream.Orders.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderBinder orderBinder;

    Map<UUID, Order> orderStore = new HashMap<>();

    public Order placeOrder(Order orderReq) {
        var order = Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .orderUUID(orderReq.getOrderUUID())
                .itemName(orderReq.getItemName())
                .build();

        orderStore.put(order.getOrderUUID(), order);
        orderBinder.inventoryCheckingOut()
                .send(MessageBuilder.withPayload(order)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .build());
        return order;

    }

    public OrderStatus statusCheck(UUID uuid) {
        return Optional.ofNullable(orderStore.get(uuid))
                .orElseThrow(() -> new OrderNotFoundException("Order Not Found")).getOrderStatus();
    }

    @StreamListener(OrderBinder.INVENTORY_CHECK_IN)
    public void checkInventory(@Payload Order orderIn) throws InterruptedException {
        log.debug("checkInventory orderIn: {}", orderIn);
        orderIn.setOrderStatus(OrderStatus.INVENTORY_CHECKING);
        orderStore.put(orderIn.getOrderUUID(), orderIn);

        Thread.sleep(5000);

        if (System.currentTimeMillis() % 2 == 0) {
            orderIn.setOrderStatus(OrderStatus.OUT_OF_STOCK);
            orderStore.put(orderIn.getOrderUUID(), orderIn);
            log.info("Let's assume we ran out of stock for item: {}", orderIn.getItemName());

            Thread.sleep(5000);
            throw new OrderFailedException(String.format("insufficient inventory for order: %s", orderIn.getOrderUUID()));
        }

        orderBinder.shippingOut()
                .send(MessageBuilder.withPayload(orderIn)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .build());
    }

    @StreamListener(OrderBinder.SHIPPING_IN)
    public void shipIt(@Payload Order order) {
        log.info("shipIt orderIn: {}", order);
        order.setOrderStatus(OrderStatus.SHIPPED);
        orderStore.put(order.getOrderUUID(), order);

        log.info("ItemID: {} has been Shipped", order.getOrderUUID());
    }

    @StreamListener(OrderBinder.ORDER_DLQ)
    public void cancelOrder(@Payload Order order) {
        log.info("cancelOrder orderIn: {}", order);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderStore.put(order.getOrderUUID(), order);
    }

}
