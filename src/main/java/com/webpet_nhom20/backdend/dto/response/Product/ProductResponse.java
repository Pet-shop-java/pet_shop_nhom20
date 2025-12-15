package com.webpet_nhom20.backdend.dto.response.Product;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webpet_nhom20.backdend.dto.response.ProductImage.ProductImageResponse;
import com.webpet_nhom20.backdend.dto.response.ProductVariant.ProductVariantResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    int id;
    int categoryId;
    String categoryName;
    String name;
    String shortDescription;
    String description;
    String animal;
    String brand;
    String stockQuantity;
    String soldQuantity;
    String isDeleted;
    String isFeatured;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;
    List<ProductImageResponse> productImage;
    List<ProductVariantResponse> productVariant;
}
