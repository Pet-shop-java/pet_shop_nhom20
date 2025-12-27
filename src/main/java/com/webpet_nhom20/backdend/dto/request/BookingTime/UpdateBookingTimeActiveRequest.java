package com.webpet_nhom20.backdend.dto.request.BookingTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBookingTimeActiveRequest {

    @NotNull
    int serviceId;
    @NotNull(message = "START_TIME_IS_NOT_NULL")
    LocalTime time;
    @Pattern(regexp = "^[01]?$", message = "IS_ACTIVE_INVALID")
    String isDeleted;
}
