package com.webpet_nhom20.backdend.dto.response.ServiceAppointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webpet_nhom20.backdend.enums.AppoinmentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceAppointmentsResponse {
    private int id;
    /* ===== SERVICE ===== */
    private int serviceId;
    private String serviceName;
    /* ===== BOOKING SLOT ===== */
    private int bookingTimeId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    /* ===== USER ===== */
    private int userId;
    /* ===== PET INFO ===== */
    private String namePet;
    private String speciePet;
    /* ===== APPOINTMENT TIME (SNAPSHOT) ===== */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentEnd;

    private AppoinmentStatus status;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;
}
