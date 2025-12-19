package com.webpet_nhom20.backdend.service;


public interface OtpService {
    void sendOtp(String email);
    void verifyOtp(String email, String otp);
}

