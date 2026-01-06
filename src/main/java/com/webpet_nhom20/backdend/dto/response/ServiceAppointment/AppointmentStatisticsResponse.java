package com.webpet_nhom20.backdend.dto.response.ServiceAppointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatisticsResponse {
    private long todayCount; // Số lịch hẹn hôm nay
    private long scheduledCount; // Số lịch đã đặt (SCHEDULED)
    private long completedCount; // Số lịch hoàn thành (COMPLETED)
    private long totalCount; // Tổng số lịch hẹn
}
