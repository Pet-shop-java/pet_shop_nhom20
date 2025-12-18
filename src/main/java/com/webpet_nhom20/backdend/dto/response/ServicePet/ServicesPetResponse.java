package com.webpet_nhom20.backdend.dto.response.ServicePet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicesPetResponse {

    int id;
    String name;
    String title;
    String description;
    Integer durationMinutes;
    BigDecimal price;
    String isActive;
    List<BookingTimeResponse> bookingTimes;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedDate;
}
