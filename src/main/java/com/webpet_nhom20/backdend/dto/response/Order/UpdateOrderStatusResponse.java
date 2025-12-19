package com.webpet_nhom20.backdend.dto.response.Order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusResponse {
    private List<String> successOrder;
    private List<String> failOrder;
}
