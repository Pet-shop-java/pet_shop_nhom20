package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Order.CheckStockRequest;
import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.response.Order.OrderDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.entity.Order;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    public OrderResponse createOrder(OrderRequest request);

    public Page<OrderResponse> getAllOrder(String status, Pageable pageable);
    public String cancelOrder(String orderCode) throws AppException;
    public void checkStock(CheckStockRequest request) ;
    public List<OrderDetailResponse> getOrderDetails(Integer orderId);

//    @Transactional
//    public void markPaid(String orderCode) ;
//
//    @Transactional
//    public void markFailed(String orderCode);
}
