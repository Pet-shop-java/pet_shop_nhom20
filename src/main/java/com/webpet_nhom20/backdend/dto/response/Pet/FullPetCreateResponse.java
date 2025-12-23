package com.webpet_nhom20.backdend.dto.response.Pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullPetCreateResponse {
    private Integer petId;
    private String message;
}
