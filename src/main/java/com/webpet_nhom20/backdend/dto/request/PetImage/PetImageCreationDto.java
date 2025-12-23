package com.webpet_nhom20.backdend.dto.request.PetImage;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PetImageCreationDto {

    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;

    private String publicId;

    private boolean isPrimary ;

    private int imagePosition = 0;
}
