package com.webpet_nhom20.backdend.dto.response.Order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.webpet_nhom20.backdend.dto.response.OrderItem.OrderItemResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    int id;

    String orderCode;

    int userId;

    BigDecimal totalAmount;

    BigDecimal shippingAmount;

    double discountPercent;

    String shippingAddress;

    String note;

    String status;

    String isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;

    List<OrderItemResponse> items;

}
