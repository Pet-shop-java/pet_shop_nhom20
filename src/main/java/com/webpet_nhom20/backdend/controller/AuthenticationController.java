package com.webpet_nhom20.backdend.controller;

import com.nimbusds.jose.JOSEException;
import com.webpet_nhom20.backdend.dto.request.Auth.*;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Auth.AuthenticationResponse;
import com.webpet_nhom20.backdend.dto.response.Auth.IntrospectResponse;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.service.AuthenticationService;
import com.webpet_nhom20.backdend.service.OtpService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    OtpService otpService;
    @PostMapping("verify-otp")
    ApiResponse<String> verifyOtp(@RequestParam String identifier, @RequestParam String otp) {
        otpService.verifyOtp(identifier, otp);
        return ApiResponse.<String>builder()
                .result("OTP verified successfully")
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }
    @PostMapping("send-otp")
    ApiResponse<String> sendOtp(@Valid @RequestBody ForgotPasswordRequest request){
        authenticationService.SendMailForgotPassword(request);
        return ApiResponse.<String>builder()
                .result("OTP sent to email successfully")
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }
    @PostMapping("change-password")
    ApiResponse<String> changePassword(@Valid @RequestBody AuthenticationRequest request){
        authenticationService.ChangePassword(request);
        return ApiResponse.<String>builder()
                .result("Change password successfully")
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }


    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@Valid @RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .success(true)
                .result(result)
                .build();
    }
    @PostMapping("/refresh-token")
    ApiResponse<AuthenticationResponse> refresh(@Valid @RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .result(result)
                .build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().success(true).build();
    }
}
