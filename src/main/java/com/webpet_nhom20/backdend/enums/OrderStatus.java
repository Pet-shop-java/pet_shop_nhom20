package com.webpet_nhom20.backdend.enums;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    WAITING_PAYMENT,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED;

    public static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSACTION =
        Map.of(
                WAITING_PAYMENT , Set.of(PROCESSING,COMPLETED),
                PROCESSING, Set.of(SHIPPED, CANCELLED),
                SHIPPED, Set.of(DELIVERED),
                DELIVERED, Set.of(COMPLETED)
        );

    public static boolean canTransition(
            OrderStatus current,
            OrderStatus next
    ){
        return VALID_TRANSACTION.getOrDefault(current , Set.of()).contains(next);
    }

}
