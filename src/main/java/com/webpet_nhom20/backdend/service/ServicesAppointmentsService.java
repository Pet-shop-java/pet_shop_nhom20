package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.CancelServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.ServiceAppointmentsRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UpdateServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UserServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ServicesAppointmentsService {
    public ServiceAppointmentsResponse create(ServiceAppointmentsRequest request);

    public Page<ServiceAppointmentsResponse> getAppointmentsByRole(UserServiceAppointmentRequest request, Pageable pageable);
    
    public ServiceAppointmentsResponse update( UpdateServiceAppointmentRequest request, String token);

    public ServiceAppointmentsResponse cancel(CancelServiceAppointmentRequest request, String token);
}
