package com.webpet_nhom20.backdend.dto.request.Order;


import com.webpet_nhom20.backdend.dto.request.OrderItem.OrderItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class CheckStockRequest {
    private List<OrderItemRequest> items;
}
