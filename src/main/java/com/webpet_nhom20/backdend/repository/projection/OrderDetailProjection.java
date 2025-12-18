package com.webpet_nhom20.backdend.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

public interface OrderDetailProjection {
    // --- Order Info ---
    Integer getOrderPrimaryId();
    String getOrderCode();
    String getStatus();
    BigDecimal getTotalAmount();
    String getShippingAddress();
    Date getOrderDate();

    // --- Item Info ---
    Integer getOrderItemId();
    Integer getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getTotalPrice();

    // --- Product Info ---
    String getProductName();

    // --- Variant Info ---
    Integer getVariantId();
    BigDecimal getPrice();          // Cột 'price'
    Integer getStockQuantity();     // Cột 'stock_quantity'     // Cột 'color' (nếu có)

    // --- Image Info ---
    Integer getImageId();
    String getImageUrl();
}