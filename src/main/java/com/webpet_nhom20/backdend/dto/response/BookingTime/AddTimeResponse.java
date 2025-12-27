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
public class AddTimeResponse {

    private int serviceId;
    private LocalTime time;
    private List<AddBookingTimeItem> addSlots;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddBookingTimeItem {
        private int id;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer maxCapacity;
        private Integer bookedCount;
        private Integer availableCount;
        private String isActive;
        private String isDeleted;
    }
}
