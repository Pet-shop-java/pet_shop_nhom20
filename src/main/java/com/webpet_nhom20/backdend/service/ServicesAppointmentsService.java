package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.*;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.AppointmentStatisticsResponse;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ServicesAppointmentsService {
    public ServiceAppointmentsResponse create(ServiceAppointmentsRequest request);

    public Page<ServiceAppointmentsResponse> getAppointmentsByRole(UserServiceAppointmentRequest request,
            Pageable pageable);

    public ServiceAppointmentsResponse update(UpdateServiceAppointmentRequest request, String token);

    public ServiceAppointmentsResponse cancel(CancelServiceAppointmentRequest request, String token);

    public ServiceAppointmentsResponse createByEmail(AdminCreateServiceAppointmentRequest request);

    // New methods for filtering and statistics
    public Page<ServiceAppointmentsResponse> getAppointmentsWithFilter(FilterAppointmentRequest request,
            Pageable pageable);

    public AppointmentStatisticsResponse getStatistics();
}
