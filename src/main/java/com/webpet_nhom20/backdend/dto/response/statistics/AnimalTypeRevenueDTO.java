package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for revenue by animal type statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimalTypeRevenueDTO {

    /** Animal type (dog, cat, hamster, bird, fish, etc.) */
    String animalType;

    /** Total revenue from products for this animal type */
    BigDecimal totalRevenue;

    /** Number of orders containing products for this animal type */
    Long orderCount;

    /** Total quantity sold for this animal type */
    Long totalQuantitySold;
}
