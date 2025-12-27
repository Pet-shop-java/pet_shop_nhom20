package com.webpet_nhom20.backdend.service;


import com.webpet_nhom20.backdend.dto.request.User.UserCreationRequest;

public interface OtpService {
    void sendOtp(UserCreationRequest request, String email);
    void verifyOtp(String email, String otp);
    public void sendOtpForgotPassword(String email);
}

