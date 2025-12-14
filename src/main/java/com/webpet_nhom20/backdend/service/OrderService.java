package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
