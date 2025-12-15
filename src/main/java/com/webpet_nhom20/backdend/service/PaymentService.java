package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface PaymentService {
    public PaymentResponseDTO createPayment(HttpServletRequest req,long amount,String bankCode) throws UnsupportedEncodingException;
}
