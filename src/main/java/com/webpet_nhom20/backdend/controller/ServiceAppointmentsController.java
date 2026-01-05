package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.CancelServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.ServiceAppointmentsRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UpdateServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UserServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import com.webpet_nhom20.backdend.service.ServicesAppointmentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody ServiceAppointmentsRequest request
    ) {
        ServiceAppointmentsResponse response = servicesAppointmentsService.create(request);

        return ResponseEntity.ok(
                ApiResponse.<ServiceAppointmentsResponse>builder()
                        .code(1000)
                        .success(true)
                        .message("Tạo lịch hẹn thành công")
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/list")
    public ResponseEntity<ApiResponse<List<ServiceAppointmentsResponse>>> getAppointmentsByRole(
            @RequestBody @Valid UserServiceAppointmentRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceAppointmentsResponse> appointments = servicesAppointmentsService.getAppointmentsByRole(request, pageable);
        ApiResponse<List<ServiceAppointmentsResponse>> response = ApiResponse.<List<ServiceAppointmentsResponse>>builder()
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
            @RequestHeader("Authorization") String token
    ) {
        ServiceAppointmentsResponse response = servicesAppointmentsService.update(request, token);

        return ResponseEntity.ok(
                ApiResponse.<ServiceAppointmentsResponse>builder()
                        .success(true)
                        .message("Update appointment successfully")
                        .result(response)
                        .build()
        );
    }
    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse<ServiceAppointmentsResponse>> cancelAppointment(
            @Valid @RequestBody CancelServiceAppointmentRequest request,
            @RequestHeader("Authorization") String token
    ) {
        ServiceAppointmentsResponse response = servicesAppointmentsService.cancel(request, token);

        return ResponseEntity.ok(
                ApiResponse.<ServiceAppointmentsResponse>builder()
                        .success(true)
                        .message("Cancel appointment successfully")
                        .result(response)
                        .build()
        );
    }


}