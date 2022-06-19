package com.demo.stream.Orders.controller;

import com.demo.stream.Orders.model.Order;
import com.demo.stream.Orders.model.OrderStatus;
import com.demo.stream.Orders.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("order")
    public Order placeOrder(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    @GetMapping("order/status/{orderUuid}")
    public OrderStatus statusCheck(@PathVariable("orderUUID") UUID orderUUID) {
        return orderService.statusCheck(orderUUID);
    }

}
