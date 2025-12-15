package com.webpet_nhom20.backdend.controller;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.webpet_nhom20.backdend.config.VnPayConfig;
import com.webpet_nhom20.backdend.dto.request.Payment.CreateVnPayPaymentRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import com.webpet_nhom20.backdend.service.OrderService;
import com.webpet_nhom20.backdend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/create-payment")
    public ApiResponse<PaymentResponseDTO> createPayment(@RequestBody CreateVnPayPaymentRequest request,
                                                         HttpServletRequest httpReq) throws Exception {
        Integer orderId = request.getOrderId();
        PaymentResponseDTO result = paymentService.createPayment(orderId,httpReq);
        return ApiResponse.<PaymentResponseDTO>builder()
                .success(true)
                .message("Successfully")
                .result(result)
                .build();
    }


    @GetMapping("/vnpay/return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean valid = VnPayConfig.verify(request);

        String responseCode = request.getParameter("vnp_ResponseCode");
        String orderCode = request.getParameter("vnp_TxnRef");

        if (valid && "00".equals(responseCode)) {
            orderService.markPaid(orderCode);
            response.sendRedirect("http://localhost:3000/payment-success");
        } else {
            orderService.markFailed(orderCode);
            response.sendRedirect("http://localhost:3000/payment-failed");
        }
    }


}
