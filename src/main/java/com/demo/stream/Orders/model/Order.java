package com.demo.stream.Orders.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private UUID orderUUID;

    private String itemName;

    private OrderStatus orderStatus;
}
