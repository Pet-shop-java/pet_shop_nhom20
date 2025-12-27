package com.webpet_nhom20.backdend.mapper;

import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.ServiceAppointmentsRequest;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import com.webpet_nhom20.backdend.entity.ServiceAppointments;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ServiceAppointmentMapper {

    @Autowired
    private ServicesPetRepository servicesPetRepository;

    @Autowired
    private UserRepository userRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentEnd", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(source = "appointmentStart", target = "appointmentStart")
    @Mapping(source = "serviceId", target = "service")
    @Mapping(source = "userId", target = "user")
    public abstract ServiceAppointments toEntity(ServiceAppointmentsRequest request);

    public abstract ServiceAppointmentsResponse toResponse(ServiceAppointments entity);

    protected ServicesPet mapServiceIdToService(Integer serviceId) {
        if (serviceId == null) {
            return null;
        }
        return servicesPetRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    protected User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Condition
    protected boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
