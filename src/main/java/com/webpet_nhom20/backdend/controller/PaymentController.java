package com.webpet_nhom20.backdend.controller;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.webpet_nhom20.backdend.config.VnPayConfig;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import com.webpet_nhom20.backdend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/create-payment")
    public ApiResponse<PaymentResponseDTO> createPayment(HttpServletRequest req) throws Exception {
        long amount = Integer.parseInt(req.getParameter("amount")) * 100L;
        String bankCode = req.getParameter("bankCode");
        PaymentResponseDTO result = paymentService.createPayment(req,amount,bankCode);
        return ApiResponse.<PaymentResponseDTO>builder()
                .success(true)
                .message("Successfully")
                .result(result)
                .build();
    }


}
