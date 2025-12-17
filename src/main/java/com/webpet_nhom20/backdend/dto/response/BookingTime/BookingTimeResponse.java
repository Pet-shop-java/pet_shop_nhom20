package com.webpet_nhom20.backdend.dto.response.BookingTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingTimeResponse {
    int id;
    LocalDate slotDate;
    LocalTime startTime;
    LocalTime endTime;
    int maxCapacity;
    int bookedCount;
    int availableCount;
}
