package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Order.CheckStockRequest;
import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.request.Order.UpdateOrderStatusRequest;
import com.webpet_nhom20.backdend.dto.response.Order.OrderDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.dto.response.Order.UpdateOrderStatusResponse;
import com.webpet_nhom20.backdend.entity.Order;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    public OrderResponse createOrder(OrderRequest request);

    public Page<OrderResponse> getAllOrderForUser(String status, Pageable pageable);
    public String cancelOrder(String orderCode) throws AppException;
    public void checkStock(CheckStockRequest request) ;
    public List<OrderDetailResponse> getOrderDetails(Integer orderId);
    public Page<OrderResponse> searchOrders(
            String orderCode,
            String status,
            String address,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );
    public UpdateOrderStatusResponse updateOrderStatus (UpdateOrderStatusRequest request);
//    @Transactional
//    public void markPaid(String orderCode) ;
//
//    @Transactional
//    public void markFailed(String orderCode);
}
