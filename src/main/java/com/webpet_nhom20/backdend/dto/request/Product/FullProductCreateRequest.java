package com.webpet_nhom20.backdend.dto.request.Product;

import com.webpet_nhom20.backdend.dto.request.Product_Image.ImageCreateDto;
import com.webpet_nhom20.backdend.dto.request.Product_Variant.VariantCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class FullProductCreateRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    private Integer categoryId;
    private String shortDescription;
    private String description;
    private String animal;
    private String brand;
    private boolean isFeatured;

    @Valid // Kích hoạt validation cho các đối tượng bên trong List
    @NotEmpty(message = "Product must have at least one image")
    private List<ImageCreateDto> images;

    @Valid
    @NotEmpty(message = "Product must have at least one variant")
    private List<VariantCreateDto> variants;
}
