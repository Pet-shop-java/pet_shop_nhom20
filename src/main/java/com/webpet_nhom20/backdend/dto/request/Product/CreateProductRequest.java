package com.webpet_nhom20.backdend.dto.request.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {

    /**
     * ID danh mục sản phẩm thuộc về
     * - Không được để trống
     */
    @NotNull(message = "CATEGORY_ID_NOT_NULL")
    int categoryId;
    
    /**
     * Tên sản phẩm
     * - Không được để trống
     * 
     * Ví dụ: Thức ăn cho chó Royal Canin, Đồ chơi bóng cho mèo
     */
    @NotBlank(message = "PRODUCT_NAME_IS_NOT_NULL")
    String name;
    
    /**
     * Mô tả ngắn về sản phẩm (tùy chọn)
     */
    String shortDescription;
    
    /**
     * Mô tả chi tiết về sản phẩm (tùy chọn)
     */
    String description;
    String animal;
    String brand;
}
