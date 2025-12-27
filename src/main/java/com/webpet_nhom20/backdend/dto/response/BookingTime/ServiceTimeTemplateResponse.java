package com.webpet_nhom20.backdend.dto.response.BookingTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTimeTemplateResponse {

    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCapacity;
    private String isDeleted;
}
