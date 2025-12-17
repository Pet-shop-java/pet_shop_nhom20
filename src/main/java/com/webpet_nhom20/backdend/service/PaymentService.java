package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Payment.CreateVnPayPaymentRequest;
import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentService {
    public PaymentResponseDTO createPayment(Integer  orderId, HttpServletRequest httpReq) throws UnsupportedEncodingException;
    public void handleVnPaySuccess(HttpServletRequest request) throws Exception;
    public void handleVnPayFailed(HttpServletRequest request) throws Exception;
}
