package com.demo.stream.Orders.service;

import com.demo.stream.Orders.model.Order;
import com.demo.stream.Orders.model.OrderBinder;
import com.demo.stream.Orders.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderBinder orderBinder;
    public  Order placeOrder(Order order) {
    }

    public OrderStatus statusCheck(UUID uuid) {
    }
}
