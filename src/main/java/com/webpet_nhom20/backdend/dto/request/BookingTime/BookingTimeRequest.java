package com.webpet_nhom20.backdend.dto.request.BookingTime;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingTimeRequest {

    @NotNull(message = "START_TIME_IS_NOT_NULL")
    LocalTime startTime;

    @NotNull(message = "MAX_CAPACITY_IS_NOT_NULL")
    Integer maxCapacity;
}
