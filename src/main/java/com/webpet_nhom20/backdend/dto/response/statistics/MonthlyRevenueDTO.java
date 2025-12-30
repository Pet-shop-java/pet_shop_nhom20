package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for monthly revenue statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyRevenueDTO {

    /** Year of the month */
    Integer year;

    /** Month number (1-12) */
    Integer month;

    /** Formatted month string (yyyy-MM) */
    String monthLabel;

    /** Total revenue for the month */
    BigDecimal revenue;

    /** Number of orders in the month */
    Long orderCount;
}
