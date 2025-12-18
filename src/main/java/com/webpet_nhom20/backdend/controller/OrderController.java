package com.webpet_nhom20.backdend.controller;


import com.webpet_nhom20.backdend.dto.request.Order.CheckStockRequest;
import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public ApiResponse<Page<OrderResponse>> getAllOrderForUser(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable, @RequestParam(required = false) String status) {
        Page<OrderResponse> orderResponse = orderService.getAllOrderForUser(status, pageable);
        return ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .message("successfully")
                .result(orderResponse)
                .build();
    }


    @PutMapping("cancel/{orderCode}")
    public ApiResponse<String> cancelOrder(@PathVariable String orderCode){
        String result = orderService.cancelOrder(orderCode);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Cancel order successfully")
                .result(result)
                .build();
    }
    @PostMapping("/check-stock")
    public ApiResponse<Void> checkStock(@RequestBody CheckStockRequest request){
        orderService.checkStock(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("")
                .build();
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<List<OrderDetailResponse>> getOrderDetails(@PathVariable int id) {
        List<OrderDetailResponse> result = orderService.getOrderDetails(id);

        return ApiResponse.<List<OrderDetailResponse>>builder()
                .success(true)
                .message("Lấy chi tiết đơn hàng thành công")
                .result(result)
                .build();
    }


    @GetMapping("/admin/orders")
    public ApiResponse<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String address,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime toDate,

            Pageable pageable
    ) {
        Page<OrderResponse> response = orderService.searchOrders(
                orderCode, status, address, fromDate, toDate, pageable
        );
        return ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .message("successfully")
                .result(response)
                .build();
    }

}
