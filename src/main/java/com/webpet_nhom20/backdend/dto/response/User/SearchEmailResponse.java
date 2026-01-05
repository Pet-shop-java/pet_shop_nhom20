package com.webpet_nhom20.backdend.dto.response.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEmailResponse {

    private List<String> emails;
}
