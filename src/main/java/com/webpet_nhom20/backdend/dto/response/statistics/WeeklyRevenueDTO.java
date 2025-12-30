package com.webpet_nhom20.backdend.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyRevenueDTO {
    private Integer year;
    private Integer weekNumber;
    private String weekLabel;
    private BigDecimal revenue;
    private Long orderCount;
}
