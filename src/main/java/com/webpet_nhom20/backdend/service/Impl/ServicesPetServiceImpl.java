package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.BookingTime.BookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.UpdateBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.CreateServicePetRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.UpdateServicePetRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.ServiceTimeTemplateResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.CheckNameResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.ServicesPetResponse;
import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.entity.ServiceAppointments;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ServicesPetMapper;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesAppointmentsRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.service.ServicesPetService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicesPetServiceImpl implements ServicesPetService {

    @Autowired
    private final ServicesPetRepository servicesPetRepository;

    @Autowired
    private final ServicesAppointmentsRepository servicesAppointmentsRepository;
    @Autowired
    private final ServicesPetMapper servicesPetMapper;

    @Autowired
    private final BookingTimeRepository bookingTimeRepository;

    @Override
    public Page<ServicesPetResponse> getAllServices(
            String search,
            Pageable pageable
    ) {
        Page<ServicesPet> page;

        if (search == null || search.trim().isEmpty()) {
            page = servicesPetRepository
                    .findAllOrderByActiveAndCreated(pageable);
        } else {
            page = servicesPetRepository
                    .searchOrderByActiveAndCreated(
                            search.trim(),
                            pageable
                    );
        }

        List<ServicesPetResponse> responses = page.getContent()
                .stream()
                .map(service -> {
                    List<BookingTime> bookingTimes =
                            bookingTimeRepository.findByService_IdAndIsActive(service.getId(), "1");

                    return mapServiceWithTimeTemplates(service, bookingTimes);
                })
                .toList();

        return new PageImpl<>(
                responses,
                pageable,
                page.getTotalElements()
        );
    }

    @Override
    public ServicesPetResponse getServiceById(int serviceId) {
        ServicesPet servicesPet = servicesPetRepository.findById(serviceId).orElseThrow(()-> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        return servicesPetMapper.toServicesPetResponse(servicesPet);
    }

    @Override
    public CheckNameResponse checkServiceTitle(String title) {

        boolean exists = servicesPetRepository.existsByTitle(title);

        if (exists) {
            return new CheckNameResponse(
                    false,
                    "Tên dịch vụ đã tồn tại"
            );
        }

        return new CheckNameResponse(
                true,
                "Tên dịch vụ hợp lệ"
        );
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

        boolean titleExists =
                servicesPetRepository.existsByTitle(request.getTitle());

        if (titleExists) {
            throw new AppException(ErrorCode.SERVICE_TITLE_ALREADY_EXISTS);
        }

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
                slot.setIsDeleted("0");

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
    @Transactional
    public ServicesPetResponse updateServicesPet(
            int servicePetId,
            UpdateServicePetRequest request
    ) {

        // 1️⃣ Load service
        ServicesPet service = servicesPetRepository.findById(servicePetId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (request.getTitle() != null) {

            boolean titleExists =
                    servicesPetRepository.existsByTitleAndIdNot(
                            request.getTitle(),
                            servicePetId
                    );

            if (titleExists) {
                throw new AppException(ErrorCode.SERVICE_TITLE_ALREADY_EXISTS);
            }
        }

        // 2️⃣ Detect duration change
        Integer oldDuration = service.getDurationMinutes();

        // 3️⃣ Update service info
        servicesPetMapper.updateServicePet(service, request);
        servicesPetRepository.save(service);

        boolean durationChanged =
                request.getDurationMinutes() != null
                        && !request.getDurationMinutes().equals(oldDuration);

        // 4️⃣ Không update booking time
        if (request.getBookingTimeUpdates() == null
                || request.getBookingTimeUpdates().isEmpty()) {

            // Nếu chỉ đổi duration → update toàn bộ endTime
            if (durationChanged) {
                updateEndTimeForAllActiveSlots(service);
            }

            List<BookingTime> activeTimes =
                    bookingTimeRepository.findByService_IdAndIsActive(
                            servicePetId, "1"
                    );

            return mapServiceWithTimeTemplates(service, activeTimes);
        }

        // 5️⃣ Update booking times
        for (UpdateBookingTimeRequest btReq : request.getBookingTimeUpdates()) {

            LocalTime oldTime = btReq.getOldTime();
            LocalTime newTime = btReq.getNewTime();           // nullable
            Integer newMaxCapacity = btReq.getMaxCapacity(); // nullable

            // 5.1 Lấy toàn bộ slot ACTIVE theo oldTime
            List<BookingTime> slots =
                    bookingTimeRepository.findByService_IdAndStartTimeAndIsActive(
                            servicePetId,
                            oldTime,
                            "1"
                    );

            if (slots.isEmpty()) {
                throw new AppException(ErrorCode.BOOKING_TIME_NOT_FOUND);
            }

            for (BookingTime slot : slots) {

                // 5.2 Check trùng giờ nếu có đổi giờ
                if (newTime != null && !newTime.equals(oldTime)) {

                    boolean exists =
                            bookingTimeRepository
                                    .existsByService_IdAndSlotDateAndStartTimeAndIsActiveTrue(
                                            servicePetId,
                                            slot.getSlotDate(),
                                            newTime
                                    );

                    if (exists) {
                        throw new AppException(ErrorCode.BOOKING_TIME_ALREADY_EXISTS);
                    }
                }

                List<ServiceAppointments> appointments =
                        servicesAppointmentsRepository.findByBookingTimeId(slot.getId());

                boolean hasBooking = !appointments.isEmpty();

                // 6️⃣ CHƯA CÓ BOOK → update trực tiếp
                if (!hasBooking) {

                    if (newTime != null) {
                        slot.setStartTime(newTime);
                    }

                    if (newMaxCapacity != null) {
                        slot.setMaxCapacity(newMaxCapacity);
                    }

                    slot.setEndTime(
                            slot.getStartTime()
                                    .plusMinutes(service.getDurationMinutes())
                    );

                    slot.setBookedCount(0);
                    slot.setAvailableCount(slot.getMaxCapacity());
                    slot.setIsActive("1");

                    bookingTimeRepository.save(slot);
                    continue;
                }

                // 7️⃣ ĐÃ CÓ BOOK → FREEZE SLOT CŨ
                BookingTime frozenSlot = new BookingTime();
                frozenSlot.setService(service);
                frozenSlot.setSlotDate(slot.getSlotDate());
                frozenSlot.setStartTime(slot.getStartTime());
                frozenSlot.setEndTime(slot.getEndTime());
                frozenSlot.setMaxCapacity(slot.getMaxCapacity());
                frozenSlot.setBookedCount(slot.getBookedCount());
                frozenSlot.setAvailableCount(0);
                frozenSlot.setIsActive("0");

                BookingTime savedFrozen =
                        bookingTimeRepository.save(frozenSlot);

                // 7.1 Chuyển appointment sang frozen slot
                for (ServiceAppointments ap : appointments) {
                    ap.setBookingTime(savedFrozen);
                    servicesAppointmentsRepository.save(ap);
                }

                // 8️⃣ SLOT GỐC → TEMPLATE MỚI
                if (newTime != null) {
                    slot.setStartTime(newTime);
                }

                if (newMaxCapacity != null) {
                    slot.setMaxCapacity(newMaxCapacity);
                }

                slot.setEndTime(
                        slot.getStartTime()
                                .plusMinutes(service.getDurationMinutes())
                );

                slot.setBookedCount(0);
                slot.setAvailableCount(slot.getMaxCapacity());
                slot.setIsActive("1");

                bookingTimeRepository.save(slot);
            }
        }

        // 9️⃣ FIX BUG: duration đổi → update END TIME cho TOÀN BỘ slot ACTIVE
        if (durationChanged) {
            updateEndTimeForAllActiveSlots(service);
        }

        // 🔟 Response
        List<BookingTime> activeTimes =
                bookingTimeRepository.findByService_IdAndIsActive(
                        servicePetId, "1"
                );

        return mapServiceWithTimeTemplates(service, activeTimes);
    }

    private void updateEndTimeForAllActiveSlots(ServicesPet service) {

        List<BookingTime> activeSlots =
                bookingTimeRepository.findByService_IdAndIsActive(
                        service.getId(), "1"
                );

        for (BookingTime slot : activeSlots) {
            slot.setEndTime(
                    slot.getStartTime()
                            .plusMinutes(service.getDurationMinutes())
            );
        }

        bookingTimeRepository.saveAll(activeSlots);
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

    private ServicesPetResponse mapServiceWithTimeTemplates(
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

        List<ServiceTimeTemplateResponse> timeTemplates =
                bookingTimes.stream()
                        .collect(Collectors.toMap(
                                BookingTime::getStartTime,
                                bt -> new ServiceTimeTemplateResponse(
                                        bt.getStartTime(),
                                        bt.getEndTime(),
                                        bt.getMaxCapacity(),
                                        bt.getIsDeleted()
                                ),
                                (existing, duplicate) -> existing
                        ))
                        .values()
                        .stream()
                        .sorted(Comparator.comparing(ServiceTimeTemplateResponse::getStartTime))
                        .toList();

        response.setTimeTemplates(timeTemplates);
        return response;
    }
}
