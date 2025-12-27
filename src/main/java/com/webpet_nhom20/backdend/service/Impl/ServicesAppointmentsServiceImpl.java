package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.CancelServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.ServiceAppointmentsRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UpdateServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.request.ServiceAppointment.UserServiceAppointmentRequest;
import com.webpet_nhom20.backdend.dto.response.ServiceAppointment.ServiceAppointmentsResponse;
import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.entity.ServiceAppointments;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.common.CommonUtil;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.enums.AppoinmentStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ServiceAppointmentMapper;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesAppointmentsRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.repository.UserRepository;
import com.webpet_nhom20.backdend.service.AsyncEmailService;
import com.webpet_nhom20.backdend.service.ServicesAppointmentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicesAppointmentsServiceImpl implements ServicesAppointmentsService {
    private final ServicesPetRepository servicesPetRespository;
    private final ServicesAppointmentsRepository servicesAppointmentsRepository;
    private final ServiceAppointmentMapper serviceAppointmentMapper;
    private final UserRepository userRepository;
    private final AsyncEmailService asyncEmailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BookingTimeRepository bookingTimeRepository;

    @Override
    @Transactional
    public ServiceAppointmentsResponse create(
            ServiceAppointmentsRequest request
    ) {

        // 1. LOCK booking_time
        BookingTime bookingTime =
                bookingTimeRepository.findByIdForUpdate(
                        request.getBookingTimeId()
                ).orElseThrow(() ->
                        new RuntimeException("BookingTime không tồn tại"));

        ServicesPet service = bookingTime.getService();

        // 2. Check rule: chỉ được đặt trước durationMinutes
        LocalDateTime slotStart =
                bookingTime.getSlotDate()
                        .atTime(bookingTime.getStartTime());

        // số phút phải đặt trước
        int bookingCutoffMinutes = 30;

        LocalDateTime cutoffTime =
                slotStart.minusMinutes(bookingCutoffMinutes);

        if (!LocalDateTime.now().isBefore(cutoffTime)) {
            throw new RuntimeException(
                    "Lịch đã đóng, lịch phải được đặt trước ít nhất "
                            + bookingCutoffMinutes + " phút"
            );
        }
        // 3. Check capacity
        if (bookingTime.getAvailableCount() <= 0) {
            throw new RuntimeException("Slot đã hết chỗ");
        }

        // 4. Load user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User không tồn tại"));

        // 5. Map entity (mapper thủ công)
        ServiceAppointments appointment =
                mapToEntity(request, bookingTime, user);

        // 6. Trừ slot
        bookingTime.setBookedCount(
                bookingTime.getBookedCount() + 1
        );
        // availableCount auto update bởi @PreUpdate

        // 7. Save appointment
        ServiceAppointments saved =
                servicesAppointmentsRepository.save(appointment);

        // 8. Gửi mail (GIỮ NGUYÊN)
        try {
            String subject = CommonUtil.buildAppointmentEmailSubject(
                    saved,
                    user.getFullName(),
                    user.getPhone()
            );

            String html = CommonUtil.buildAppointmentEmailHtml(
                    saved,
                    user.getFullName(),
                    user.getPhone(),
                    service.getTitle()
            );

            asyncEmailService.sendAppointmentEmail(
                    user.getEmail(),
                    subject,
                    html
            );
        } catch (Exception ignored) {}

        // 9 Response
        return mapToResponse(saved);
    }

    @Override
    public Page<ServiceAppointmentsResponse> getAppointmentsByRole(
            UserServiceAppointmentRequest request,
            Pageable pageable
    ) {

        String role = request.getRoleName();
        Page<ServiceAppointments> page;

        if ("ADMIN".equals(role)) {

            page = servicesAppointmentsRepository
                    .findAllOrderByStatusAndNearest(pageable);

        } else if ("CUSTOMER".equals(role)) {

            page = servicesAppointmentsRepository
                    .findByUserIdOrderByStatusAndNearest(
                            request.getUserId(),
                            pageable
                    );

        } else {
            throw new RuntimeException("Invalid role: " + role);
        }

        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ServiceAppointmentsResponse update(
            UpdateServiceAppointmentRequest request,
            String token
    ) {

        Integer userId = jwtTokenProvider.getUserId(token);
        String role = jwtTokenProvider.getUserRole(token);

        if (userId == null || role == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        ServiceAppointments appointment =
                servicesAppointmentsRepository.findById(request.getId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updateDeadline =
                appointment.getAppointmentStart().minusMinutes(30);

        boolean isAfterDeadline = now.isAfter(updateDeadline);
        boolean isAfterStart = now.isAfter(appointment.getAppointmentStart());

        // ===== CHECK QUYỀN =====
        if ("CUSTOMER".equals(role)) {

            if (appointment.getUser().getId() != userId.intValue()) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }

            if (isAfterDeadline) {
                throw new AppException(ErrorCode.UPDATE_TOO_LATE);
            }

            if (request.getStatus() != null) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }

        } else if ("SHOP".equals(role)) {

            if (isAfterStart && request.getBookingTimeId() != null) {
                throw new AppException(
                        ErrorCode.APPOINTMENT_ALREADY_STARTED
                );
            }

        } else {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // ===== UPDATE BOOKING TIME =====
        if (request.getBookingTimeId() != null &&
                !request.getBookingTimeId().equals(
                        appointment.getBookingTime().getId())) {

            // 1️⃣ LOCK slot cũ
            BookingTime oldSlot =
                    bookingTimeRepository.findByIdForUpdate(
                                    appointment.getBookingTime().getId())
                            .orElseThrow();

            // 2️⃣ LOCK slot mới
            BookingTime newSlot =
                    bookingTimeRepository.findByIdForUpdate(
                                    request.getBookingTimeId())
                            .orElseThrow(() ->
                                    new AppException(ErrorCode.BOOKING_TIME_NOT_FOUND));

            ServicesPet service = newSlot.getService();

            // 3️⃣ Validate giờ đặt (giống create)
            LocalDateTime slotStart =
                    newSlot.getSlotDate().atTime(newSlot.getStartTime());

            LocalDateTime latestBookingTime =
                    slotStart.minusMinutes(service.getDurationMinutes());

            if (!now.isBefore(latestBookingTime)) {
                throw new AppException(ErrorCode.BOOKING_TOO_LATE);
            }

            if (newSlot.getAvailableCount() <= 0) {
                throw new AppException(ErrorCode.SLOT_FULL);
            }

            // 4️⃣ Trả slot cũ
            oldSlot.setBookedCount(oldSlot.getBookedCount() - 1);

            // 5️⃣ Trừ slot mới
            newSlot.setBookedCount(newSlot.getBookedCount() + 1);

            // 6️⃣ Update appointment
            appointment.setBookingTime(newSlot);
            appointment.setService(service);

            appointment.setAppointmentStart(
                    newSlot.getSlotDate().atTime(newSlot.getStartTime()));

            appointment.setAppointmentEnd(
                    newSlot.getSlotDate().atTime(newSlot.getEndTime()));
        }

        // ===== UPDATE FIELD KHÁC =====
        if (!isAfterStart) {
            appointment.setNamePet(request.getNamePet());
            appointment.setSpeciePet(request.getSpeciePet());

            if (request.getNotes() != null) {
                appointment.setNotes(request.getNotes());
            }
        }

        // Shop update status
        if ("SHOP".equals(role) && request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }

        ServiceAppointments saved =
                servicesAppointmentsRepository.save(appointment);

        return mapToResponse(saved);
    }



    @Override
    @Transactional
    public ServiceAppointmentsResponse cancel(
            CancelServiceAppointmentRequest request,
            String token
    ) {

        // 1️⃣ Xác thực token
        Integer userIdFromToken = jwtTokenProvider.getUserId(token);
        if (userIdFromToken == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 2️⃣ Lấy appointment
        ServiceAppointments existingAppointment =
                servicesAppointmentsRepository.findById(request.getId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        // 3️⃣ Check đúng chủ lịch
        if(existingAppointment.getUser().getId() != userIdFromToken.longValue()){
            throw new AppException(ErrorCode.UNAUTHENTICATED_CANCEL); }

        // 4️⃣ Không cho hủy nếu đã COMPLETED / CANCELED
        if (existingAppointment.getStatus() == AppoinmentStatus.CANCELED) {
            throw new AppException(ErrorCode.ALREADY_CANCELED);
        }

        if (existingAppointment.getStatus() == AppoinmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.ALREADY_COMPLETED);
        }

        // 5️⃣ ❗ Check thời gian: đã quá giờ bắt đầu thì không cho hủy
        // hủy trước 30 phút
        if (LocalDateTime.now()
                .isAfter(existingAppointment
                        .getAppointmentStart()
                        .minusMinutes(30))) {
            throw new AppException(ErrorCode.CANCEL_TOO_LATE);
        }

        // 6️⃣ Update status
        existingAppointment.setStatus(AppoinmentStatus.CANCELED);
        existingAppointment.setNotes(
                "Cuộc hẹn đã bị hủy lúc " + LocalDateTime.now()
        );

        // 7️⃣ Hoàn slot (rất quan trọng)
        BookingTime bookingTime = existingAppointment.getBookingTime();
        bookingTime.setBookedCount(
                bookingTime.getBookedCount() - 1
        );
        // availableCount tự update bởi @PreUpdate

        // 8️⃣ Save
        ServiceAppointments canceledAppointment =
                servicesAppointmentsRepository.save(existingAppointment);

        // 9️⃣ Mapper thủ công
        return mapToResponse(canceledAppointment);
    }

    private ServiceAppointments mapToEntity(
            ServiceAppointmentsRequest request,
            BookingTime bookingTime,
            User user
    ) {

        ServiceAppointments a = new ServiceAppointments();

        a.setService(bookingTime.getService());
        a.setBookingTime(bookingTime);
        a.setUser(user);

        a.setNamePet(request.getNamePet());
        a.setSpeciePet(request.getSpeciePet());

        LocalDateTime start =
                bookingTime.getSlotDate()
                        .atTime(bookingTime.getStartTime());

        LocalDateTime end =
                bookingTime.getSlotDate()
                        .atTime(bookingTime.getEndTime());

        a.setAppointmentStart(start);
        a.setAppointmentEnd(end);

        a.setStatus(AppoinmentStatus.SCHEDULED);
        a.setNotes(request.getNotes());

        return a;
    }

    private ServiceAppointmentsResponse mapToResponse(
            ServiceAppointments a
    ) {

        ServiceAppointmentsResponse r =
                new ServiceAppointmentsResponse();

        r.setId(a.getId());
        r.setServiceId(a.getService().getId());
        r.setServiceName(a.getService().getTitle());
        r.setBookingTimeId(a.getBookingTime().getId());
        r.setUserId(a.getUser().getId());
        r.setNamePet(a.getNamePet());
        r.setSpeciePet(a.getSpeciePet());
        r.setAppointmentStart(a.getAppointmentStart());
        r.setAppointmentEnd(a.getAppointmentEnd());
        r.setStatus(a.getStatus());
        r.setNotes(a.getNotes());
        r.setCreatedDate(a.getCreatedDate());
        r.setUpdatedDate(a.getUpdatedDate());

        return r;
    }
}

