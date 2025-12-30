package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for top customers by spending
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopCustomerDTO {

    /** User ID */
    Integer userId;

    /** Customer full name */
    String customerName;

    /** Customer email */
    String email;

    /** Customer phone */
    String phone;

    /** Total amount spent by customer */
    BigDecimal totalSpent;

    /** Number of orders placed by customer */
    Long orderCount;
}
