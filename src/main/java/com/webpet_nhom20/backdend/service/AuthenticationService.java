package com.webpet_nhom20.backdend.service;

import com.nimbusds.jose.JOSEException;
import com.webpet_nhom20.backdend.dto.request.Auth.*;
import com.webpet_nhom20.backdend.dto.response.Auth.AuthenticationResponse;
import com.webpet_nhom20.backdend.dto.response.Auth.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {


    public AuthenticationResponse authenticate(AuthenticationRequest request);
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
    public void logout(LogoutRequest request);
    public void SendMailForgotPassword(ForgotPasswordRequest request);
    public void ChangePassword(AuthenticationRequest request);
}
