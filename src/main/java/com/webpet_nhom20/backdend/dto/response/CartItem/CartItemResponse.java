package com.webpet_nhom20.backdend.dto.response.CartItem;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {

    Integer id;
    Integer cartId;
    Integer productVariantId;
    Integer stockQuantity;
    Integer quantity;
    BigDecimal unitPrice;
    String productName;
    String variantName;
    String imageUrl;
    String isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;
}
