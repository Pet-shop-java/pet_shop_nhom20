package com.webpet_nhom20.backdend.dto.response.BookingTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingTimeActiveResponse {
    private int serviceId;
    private LocalTime time;
    private List<UpdatedBookingTimeItem> updatedSlots;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdatedBookingTimeItem {
        private int id;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String isDeleted;
    }
}
