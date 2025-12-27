package com.webpet_nhom20.backdend.dto.request.BookingTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingTimeRequest {

    @NotNull
    private Integer serviceId;

    @NotNull(message = "START_TIME_IS_NOT_NULL")
    private LocalTime startTime;

    @NotNull(message = "MAX_CAPACITY_IS_NOT_NULL")
    @Positive(message = "MAX_CAPACITY_MUST_BE_POSITIVE")
    private Integer maxCapacity;
}
