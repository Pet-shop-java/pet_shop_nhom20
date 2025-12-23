package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import com.webpet_nhom20.backdend.service.BookingTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                        .findByServiceIdAndSlotDateAndIsActive(
                                request.getServiceId(),
                                request.getDate(),
                                "1"
                        );

        //Mapper thủ công → response
        return bookingTimes.stream()
                .map(this::mapToResponse)
                .toList();
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
