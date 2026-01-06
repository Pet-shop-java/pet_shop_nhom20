package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.*;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.AppointmentStatisticsResponse;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import com.webpet_nhom20.backdend.service.ServicesAppointmentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class ServiceAppointmentsController {

        @Autowired
        private final ServicesAppointmentsService servicesAppointmentsService;

        @PostMapping
        public ResponseEntity<ApiResponse<ServiceAppointmentsResponse>> createAppointment(
                        @Valid @RequestBody ServiceAppointmentsRequest request) {
                ServiceAppointmentsResponse response = servicesAppointmentsService.create(request);

                return ResponseEntity.ok(
                                ApiResponse.<ServiceAppointmentsResponse>builder()
                                                .code(1000)
                                                .success(true)
                                                .message("Tạo lịch hẹn thành công")
                                                .result(response)
                                                .build());
        }

        @PostMapping("/admin-email")
        @PreAuthorize("hasRole('SHOP')")
        public ResponseEntity<ApiResponse<ServiceAppointmentsResponse>> createAppointmentAdmin(
                        @Valid @RequestBody AdminCreateServiceAppointmentRequest request) {
                ServiceAppointmentsResponse response = servicesAppointmentsService.createByEmail(request);

                return ResponseEntity.ok(
                                ApiResponse.<ServiceAppointmentsResponse>builder()
                                                .code(1000)
                                                .success(true)
                                                .message("Tạo lịch hẹn thành công")
                                                .result(response)
                                                .build());
        }

        @PostMapping("/list")
        public ResponseEntity<ApiResponse<List<ServiceAppointmentsResponse>>> getAppointmentsByRole(
                        @RequestBody @Valid UserServiceAppointmentRequest request,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<ServiceAppointmentsResponse> appointments = servicesAppointmentsService
                                .getAppointmentsByRole(request, pageable);
                ApiResponse<List<ServiceAppointmentsResponse>> response = ApiResponse
                                .<List<ServiceAppointmentsResponse>>builder()
                                .success(true)
                                .message("Get appointments successfully")
                                .result(appointments.getContent())
                                .currentPage(appointments.getNumber())
                                .pageSize(appointments.getSize())
                                .totalElements(appointments.getTotalElements())
                                .totalPages(appointments.getTotalPages())
                                .build();
                return ResponseEntity.ok(response);
        }

        @PutMapping("/update")
        public ResponseEntity<ApiResponse<ServiceAppointmentsResponse>> updateAppointment(
                        @Valid @RequestBody UpdateServiceAppointmentRequest request,
                        @RequestHeader("Authorization") String token) {
                ServiceAppointmentsResponse response = servicesAppointmentsService.update(request, token);

                return ResponseEntity.ok(
                                ApiResponse.<ServiceAppointmentsResponse>builder()
                                                .success(true)
                                                .message("Update appointment successfully")
                                                .result(response)
                                                .build());
        }

        @PutMapping("/cancel")
        public ResponseEntity<ApiResponse<ServiceAppointmentsResponse>> cancelAppointment(
                        @Valid @RequestBody CancelServiceAppointmentRequest request,
                        @RequestHeader("Authorization") String token) {
                ServiceAppointmentsResponse response = servicesAppointmentsService.cancel(request, token);

                return ResponseEntity.ok(
                                ApiResponse.<ServiceAppointmentsResponse>builder()
                                                .success(true)
                                                .message("Cancel appointment successfully")
                                                .result(response)
                                                .build());
        }

        // ============ NEW ENDPOINTS FOR FILTERING AND STATISTICS ============

        /**
         * Filter appointments with pagination
         * GET
         * /api/v1/appointments/filter?page=0&size=10&status=SCHEDULED&email=test@example.com&serviceId=1&fromDate=2026-01-01&toDate=2026-01-31
         */
        @GetMapping("/filter")
        @PreAuthorize("hasRole('SHOP')")
        public ResponseEntity<ApiResponse<List<ServiceAppointmentsResponse>>> filterAppointments(
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) Integer serviceId,
                        @RequestParam(required = false) String fromDate,
                        @RequestParam(required = false) String toDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                FilterAppointmentRequest request = FilterAppointmentRequest.builder()
                                .status(status)
                                .email(email)
                                .serviceId(serviceId)
                                .fromDate(fromDate)
                                .toDate(toDate)
                                .build();

                Pageable pageable = PageRequest.of(page, size);
                Page<ServiceAppointmentsResponse> appointments = servicesAppointmentsService
                                .getAppointmentsWithFilter(request, pageable);

                ApiResponse<List<ServiceAppointmentsResponse>> response = ApiResponse
                                .<List<ServiceAppointmentsResponse>>builder()
                                .success(true)
                                .message("Get filtered appointments successfully")
                                .result(appointments.getContent())
                                .currentPage(appointments.getNumber())
                                .pageSize(appointments.getSize())
                                .totalElements(appointments.getTotalElements())
                                .totalPages(appointments.getTotalPages())
                                .build();
                return ResponseEntity.ok(response);
        }

        /**
         * Get appointment statistics
         * GET /api/v1/appointments/statistics
         */
        @GetMapping("/statistics")
        @PreAuthorize("hasRole('SHOP')")
        public ResponseEntity<ApiResponse<AppointmentStatisticsResponse>> getStatistics() {
                AppointmentStatisticsResponse statistics = servicesAppointmentsService.getStatistics();

                return ResponseEntity.ok(
                                ApiResponse.<AppointmentStatisticsResponse>builder()
                                                .success(true)
                                                .message("Get statistics successfully")
                                                .result(statistics)
                                                .build());
        }
}
