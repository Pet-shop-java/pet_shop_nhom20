package com.webpet_nhom20.backdend.dto.request.ServiceAppointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterAppointmentRequest {
    private String status; // SCHEDULED, COMPLETED, CANCELED
    private String email; // Search by email
    private Integer serviceId; // Filter by service
    private String fromDate; // Format: yyyy-MM-dd
    private String toDate; // Format: yyyy-MM-dd
}
