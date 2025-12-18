package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.BookingTime.BookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.CreateServicePetRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.UpdateServicePetRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.ServicesPetResponse;
import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ServicesPetMapper;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.service.ServicesPetService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicesPetServiceImpl implements ServicesPetService {

    @Autowired
    private final ServicesPetRepository servicesPetRepository;

    @Autowired
    private final ServicesPetMapper servicesPetMapper;

    @Autowired
    private final BookingTimeRepository bookingTimeRepository;

    @Override
    public Page<ServicesPetResponse> getAllServices(String search, Pageable pageable){
        if(search == null || search.trim().isEmpty()){
            return servicesPetRepository.findAll(pageable).map(servicesPetMapper::toServicesPetResponse);
        }
        return servicesPetRepository.findByNameContainingIgnoreCase(search,pageable).map(servicesPetMapper::toServicesPetResponse);
    }

    @Override
    public ServicesPetResponse getServiceById(int serviceId) {
        ServicesPet servicesPet = servicesPetRepository.findById(serviceId).orElseThrow(()-> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        return servicesPetMapper.toServicesPetResponse(servicesPet);
    }

    @Override
    public List<ServicesPetResponse> getActiveServices() {
        return servicesPetRepository.findByIsActive("1")
                .stream()
                .map(servicesPetMapper::toServicesPetResponse)
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('SHOP')")
    @Transactional
    @Override
    public ServicesPetResponse createServicesPet(CreateServicePetRequest request){
        //Save service
        ServicesPet service = new ServicesPet();
        service.setName(request.getName());
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());

        service = servicesPetRepository.save(service);

        //Create 14 days slot
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 14; i++) {

            LocalDate slotDate = today.plusDays(i);

            for (BookingTimeRequest bt : request.getBookingTimes()) {

                BookingTime slot = new BookingTime();
                slot.setService(service);
                slot.setSlotDate(slotDate);
                slot.setStartTime(bt.getStartTime());
                slot.setEndTime(
                        bt.getStartTime()
                                .plusMinutes(service.getDurationMinutes())
                );
                slot.setMaxCapacity(bt.getMaxCapacity());
                slot.setBookedCount(0);
                slot.setAvailableCount(bt.getMaxCapacity());
                slot.setIsActive("1");

                bookingTimeRepository.save(slot);
            }
        }

        //Query DB → map response
        List<BookingTime> bookingTimes =
                bookingTimeRepository.findByServiceId(service.getId());

        return mapServiceToResponse(service, bookingTimes);
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public ServicesPetResponse updateServicesPet(int servicePetId ,UpdateServicePetRequest request){
        ServicesPet servicesPet = servicesPetRepository.findById(servicePetId).orElseThrow(()-> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        servicesPetMapper.updateServicePet(servicesPet,request);
        return servicesPetMapper.toServicesPetResponse(servicesPetRepository.save(servicesPet));
    }

    private ServicesPetResponse mapServiceToResponse(
            ServicesPet service,
            List<BookingTime> bookingTimes
    ) {

        ServicesPetResponse response = new ServicesPetResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setTitle(service.getTitle());
        response.setDescription(service.getDescription());
        response.setDurationMinutes(service.getDurationMinutes());
        response.setPrice(service.getPrice());
        response.setIsActive(service.getIsActive());
        response.setCreatedDate(service.getCreateDate());
        response.setUpdatedDate(service.getUpdateDate());

        List<BookingTimeResponse> bookingTimeResponses =
                bookingTimes.stream().map(bt -> {

                    BookingTimeResponse r = new BookingTimeResponse();
                    r.setId(bt.getId());
                    r.setSlotDate(bt.getSlotDate());
                    r.setStartTime(bt.getStartTime());
                    r.setEndTime(bt.getEndTime());
                    r.setMaxCapacity(bt.getMaxCapacity());
                    r.setBookedCount(bt.getBookedCount());
                    r.setAvailableCount(bt.getAvailableCount());
                    return r;

                }).toList();

        response.setBookingTimes(bookingTimeResponses);
        return response;
    }

}
