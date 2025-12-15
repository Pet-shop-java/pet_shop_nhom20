package com.webpet_nhom20.backdend.dto.request.Order;


import com.webpet_nhom20.backdend.dto.request.OrderItem.OrderItemRequest;
import com.webpet_nhom20.backdend.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    BigDecimal shippingAmount;

    Float discountPercent;

    String paymentMethod;

    String shippingAddress;

    String note;

    List<OrderItemRequest> items;
}
