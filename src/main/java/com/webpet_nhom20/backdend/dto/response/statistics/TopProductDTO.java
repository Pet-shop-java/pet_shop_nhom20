package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for top selling products statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopProductDTO {

    /** Product ID */
    Integer productId;

    /** Product name */
    String productName;

    /** Category name */
    String categoryName;

    /** Animal type (dog, cat, hamster, etc.) */
    String animalType;

    /** Total quantity sold */
    Long totalQuantitySold;

    /** Total revenue from this product */
    BigDecimal totalRevenue;
}
