package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for order count by status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusCountDTO {

    /**
     * Order status (WAITING_PAYMENT, PROCESSING, SHIPPED, DELIVERED, COMPLETED,
     * CANCELLED)
     */
    String status;

    /** Number of orders with this status */
    Long count;
}
