package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.BookingTime.AddBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.AvailableBookingTimeRequest;
import com.webpet_nhom20.backdend.dto.request.BookingTime.UpdateBookingTimeActiveRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.AddTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.BookingTimeResponse;
import com.webpet_nhom20.backdend.dto.response.BookingTime.UpdateBookingTimeActiveResponse;
import com.webpet_nhom20.backdend.service.BookingTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/active")
    public ResponseEntity<ApiResponse<UpdateBookingTimeActiveResponse>> updateBookingTimeActive(
            @Valid @RequestBody UpdateBookingTimeActiveRequest request
    ) {

        UpdateBookingTimeActiveResponse response =
                bookingTimeService.updateBookingTimeActive(request);

        return ResponseEntity.ok(
                ApiResponse.<UpdateBookingTimeActiveResponse>builder()
                        .success(true)
                        .message("Update booking time active status successfully")
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/add-time")
    @PreAuthorize("hasRole('SHOP')")
    public ResponseEntity<ApiResponse<AddTimeResponse>> addBookingTime(
            @Valid @RequestBody AddBookingTimeRequest request
    ) {

        AddTimeResponse response =
                bookingTimeService.addBookingTime(request);

        return ResponseEntity.ok(
                ApiResponse.<AddTimeResponse>builder()
                        .success(true)
                        .message("Add booking time successfully")
                        .result(response)
                        .build()
        );
    }
}
