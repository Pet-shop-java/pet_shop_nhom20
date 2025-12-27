package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AddBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.UpdateBookingTimeActiveRequest;
import com.webpet_nhom20.backdend.dto.response.BookingTime.AddTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.UpdateBookingTimeActiveResponse;

import java.util.List;

public interface BookingTimeService {
    List<BookingTimeResponse> getAvailableBookingTimes(
            AvailableBookingTimeRequest request
    );

    UpdateBookingTimeActiveResponse updateBookingTimeActive(
            UpdateBookingTimeActiveRequest request
    );

    AddTimeResponse addBookingTime(AddBookingTimeRequest request);
}
