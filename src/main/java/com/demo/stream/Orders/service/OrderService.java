package com.demo.stream.Orders.service;

import com.demo.stream.Orders.exception.OrderNotFoundException;
import com.demo.stream.Orders.model.Order;
import com.demo.stream.Orders.model.OrderBinder;
import com.demo.stream.Orders.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
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
}
