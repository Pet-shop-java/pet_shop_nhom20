package com.webpet_nhom20.backdend.dto.request.Order;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    @NotEmpty(message = "orderUpdateList must not be empty")
    List<OrderUpdate> orderUpdateList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderUpdate {
        Integer id;
        String orderCode;
        String orderStatus;
    }

}
