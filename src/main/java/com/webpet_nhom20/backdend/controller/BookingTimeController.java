package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.service.BookingTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking-times")
@RequiredArgsConstructor
public class BookingTimeController {

    private final BookingTimeService bookingTimeService;

    @PostMapping("/available")
    public ResponseEntity<ApiResponse<List<BookingTimeResponse>>> getAvailableBookingTimes(
            @Valid @RequestBody AvailableBookingTimeRequest request
    ) {

        List<BookingTimeResponse> response =
                bookingTimeService.getAvailableBookingTimes(request);

        return ResponseEntity.ok(
                ApiResponse.<List<BookingTimeResponse>>builder()
                        .success(true)
                        .message("Get available booking times successfully")
                        .result(response)
                        .build()
        );
    }
}
