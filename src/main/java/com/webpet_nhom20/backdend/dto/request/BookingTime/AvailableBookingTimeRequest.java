package com.webpet_nhom20.backdend.dto.request.BookingTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableBookingTimeRequest {
    @NotNull
    private Integer serviceId;

    @NotNull
    private LocalDate date;
}
