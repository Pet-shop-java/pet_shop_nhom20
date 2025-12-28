package com.webpet_nhom20.backdend.dto.response.ServicePet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckNameResponse {
    private boolean valid;
    private String message;
}
