package com.webpet_nhom20.backdend.controller;


import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @PostMapping("/create")

    public ApiResponse<OrderResponse> createOrder(
            @RequestBody @Valid OrderRequest request
    ) {

        OrderResponse orderResponse = orderService.createOrder(request);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order created successfully")
                .result(orderResponse)
                .build();
    }

    @GetMapping()
    public ApiResponse<Page<OrderResponse>> getAllOrder(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<OrderResponse> orderResponse = orderService.getAllOrder(pageable);
        return ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .message("successfully")
                .result(orderResponse)
                .build();
    }
}
