package com.webpet_nhom20.backdend.dto.response.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Integer orderId;
    private String orderCode;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private Date orderDate;

    private Integer orderItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    private String productName;

    // Thông tin variant gom lại hoặc để rời tùy bạn
    private Integer variantId;
    private BigDecimal variantPrice;
    private Integer stockQuantity;

    private String imageUrl;
}