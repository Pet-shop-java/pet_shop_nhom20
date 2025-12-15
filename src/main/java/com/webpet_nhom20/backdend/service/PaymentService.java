package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Payment.CreateVnPayPaymentRequest;
import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface PaymentService {
    public PaymentResponseDTO createPayment(Integer  orderId, HttpServletRequest httpReq) throws UnsupportedEncodingException;
}
