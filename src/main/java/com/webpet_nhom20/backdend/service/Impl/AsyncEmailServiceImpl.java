package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.service.AsyncEmailService;
import com.webpet_nhom20.backdend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service

public class  AsyncEmailServiceImpl implements AsyncEmailService {

    @Autowired
    private final EmailService emailService;

    public AsyncEmailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @Override
    public void sendAppointmentEmail(String email, String subject, String htmlBody) {
        System.out.println(">>> Bắt đầu gửi email async đến " + email);
        emailService.sendHtml(email, subject, htmlBody);
        System.out.println(">>> Gửi email xong cho " + email);
    }
}
