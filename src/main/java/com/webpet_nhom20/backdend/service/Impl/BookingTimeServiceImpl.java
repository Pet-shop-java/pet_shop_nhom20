package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AddBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.UpdateBookingTimeActiveRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.AddTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.UpdateBookingTimeActiveResponse;
import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.service.BookingTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingTimeServiceImpl implements BookingTimeService {

    private final BookingTimeRepository bookingTimeRepository;
    private final ServicesPetRepository servicesPetRepository;
    @Override
    public List<BookingTimeResponse> getAvailableBookingTimes(AvailableBookingTimeRequest request) {
        // Check date hợp lệ: hôm nay → 14 ngày
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(14);

        if (request.getDate().isBefore(today)
                || request.getDate().isAfter(maxDate)) {
            throw new AppException(ErrorCode.DATE_OUT_OF_RANGE);
        }

        //Query booking time
        List<BookingTime> bookingTimes =
                bookingTimeRepository
                        .findByServiceIdAndSlotDateAndIsActiveAndIsDeleted(
                                request.getServiceId(),
                                request.getDate(),
                                "1",
                                "0"
                        );

        //Mapper thủ công → response
        return bookingTimes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('SHOP')")
    @Transactional
    public UpdateBookingTimeActiveResponse updateBookingTimeActive(UpdateBookingTimeActiveRequest request) {
        List<BookingTime> slots =
                bookingTimeRepository.findByService_IdAndStartTime(
                        request.getServiceId(),
                        request.getTime()
                );

        if (slots.isEmpty()) {
            throw new AppException(ErrorCode.BOOKING_TIME_NOT_FOUND);
        }

        List<UpdateBookingTimeActiveResponse.UpdatedBookingTimeItem> updated =
                new ArrayList<>();

        for (BookingTime slot : slots) {

            slot.setIsDeleted(request.getIsDeleted());
            bookingTimeRepository.save(slot);

            updated.add(
                    new UpdateBookingTimeActiveResponse.UpdatedBookingTimeItem(
                            slot.getId(),
                            slot.getSlotDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            slot.getIsDeleted()
                    )
            );
        }

        return new UpdateBookingTimeActiveResponse(
                request.getServiceId(),
                request.getTime(),
                updated
        );
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    @Transactional
    public AddTimeResponse addBookingTime(AddBookingTimeRequest request) {

        ServicesPet service = servicesPetRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        // Check trùng giờ (chỉ active = 1)
        boolean exists =
                bookingTimeRepository.existsByService_IdAndStartTimeAndIsActive(
                        service.getId(),
                        request.getStartTime(),
                        "1"
                );

        if (exists) {
            throw new AppException(ErrorCode.BOOKING_TIME_ALREADY_EXISTS);
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(13); // 14 ngày

        List<AddTimeResponse.AddBookingTimeItem> slots = new ArrayList<>();

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {

            BookingTime bookingTime = new BookingTime();
            bookingTime.setService(service);
            bookingTime.setSlotDate(date);
            bookingTime.setStartTime(request.getStartTime());
            bookingTime.setEndTime(
                    request.getStartTime()
                            .plusMinutes(service.getDurationMinutes())
            );
            bookingTime.setMaxCapacity(request.getMaxCapacity());
            bookingTime.setBookedCount(0);
            bookingTime.setAvailableCount(request.getMaxCapacity());
            bookingTime.setIsActive("1");
            bookingTime.setIsDeleted("0");

            BookingTime saved = bookingTimeRepository.save(bookingTime);

            slots.add(new AddTimeResponse.AddBookingTimeItem(
                    saved.getId(),
                    saved.getSlotDate(),
                    saved.getStartTime(),
                    saved.getEndTime(),
                    saved.getMaxCapacity(),
                    saved.getBookedCount(),
                    saved.getAvailableCount(),
                    saved.getIsActive(),
                    saved.getIsDeleted()
            ));
        }

        return new AddTimeResponse(
                service.getId(),
                request.getStartTime(),
                slots
        );
    }


    private BookingTimeResponse mapToResponse(BookingTime bt) {

        return BookingTimeResponse.builder()
                .id(bt.getId())
                .slotDate(bt.getSlotDate())
                .startTime(bt.getStartTime())
                .endTime(bt.getEndTime())
                .maxCapacity(bt.getMaxCapacity())
                .bookedCount(bt.getBookedCount())
                .availableCount(bt.getAvailableCount())
                .build();
    }
}
