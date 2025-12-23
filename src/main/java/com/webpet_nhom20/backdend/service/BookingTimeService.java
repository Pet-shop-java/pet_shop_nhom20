package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;

import java.util.List;

public interface BookingTimeService {
    List<BookingTimeResponse> getAvailableBookingTimes(
            AvailableBookingTimeRequest request
    );
}
