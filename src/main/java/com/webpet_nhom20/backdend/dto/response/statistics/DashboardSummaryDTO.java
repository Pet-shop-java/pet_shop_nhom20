package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for dashboard summary statistics
 * Provides overall metrics for the admin dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardSummaryDTO {

    /** Total number of orders in the system */
    Long totalOrders;

    /** Total revenue from COMPLETED and DELIVERED orders */
    BigDecimal totalRevenue;

    /** Total number of customers */
    Long totalCustomers;

    /** Average order value (total revenue / number of paid orders) */
    BigDecimal avgOrderValue;

    /** Number of orders with COMPLETED or DELIVERED status */
    Long paidOrderCount;
}
