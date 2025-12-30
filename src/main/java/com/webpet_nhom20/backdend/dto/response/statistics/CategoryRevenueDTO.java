package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for revenue by category statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRevenueDTO {

    /** Category ID */
    Integer categoryId;

    /** Category name */
    String categoryName;

    /** Total revenue from this category */
    BigDecimal totalRevenue;

    /** Number of orders containing products from this category */
    Long orderCount;

    /** Total quantity sold in this category */
    Long totalQuantitySold;
}
