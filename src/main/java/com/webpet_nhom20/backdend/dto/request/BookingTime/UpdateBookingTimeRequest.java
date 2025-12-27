package com.webpet_nhom20.backdend.dto.request.BookingTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingTimeRequest {

    /**
     * Giờ cũ của slot (bắt buộc)
     */
    @NotNull(message = "OLD_TIME_NOT_NULL")
    private LocalTime oldTime;

    /**
     * Giờ mới của slot (optional)
     */
    private LocalTime newTime;

    /**
     * Sức chứa mới (tùy chọn)
     */
    @Positive(message = "MAX_CAPACITY_MUST_BE_POSITIVE")
    private Integer maxCapacity;
}
