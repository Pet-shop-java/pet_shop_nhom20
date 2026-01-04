package com.webpet_nhom20.backdend.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchEmailRequest {

    @NotBlank(message = "KEYWORD_NOT_BLANK")
    private String keyword;
}
