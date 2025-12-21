package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.common.CommonUtil;
import com.webpet_nhom20.backdend.entity.EmailOtp;
import com.webpet_nhom20.backdend.repository.EmailOtpRepository;
import com.webpet_nhom20.backdend.repository.UserRepository;
import com.webpet_nhom20.backdend.service.AsyncEmailService;
import com.webpet_nhom20.backdend.service.EmailService;
import com.webpet_nhom20.backdend.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final EmailOtpRepository otpRepository;
    private final UserRepository userRepository; // bảng users
    private final AsyncEmailService emailService;     // service gửi mail

    // Gửi OTP
    public void sendOtp(String email) {

        // 1. Check email đã tồn tại chưa
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được đăng ký");
        }

        // 2. Xóa OTP cũ nếu có
        otpRepository.deleteByEmail(email);

        // 3. Sinh OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpireTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(emailOtp);

        // 4. Build email OTP
        String subject = CommonUtil.buildOtpEmailSubject("Đăng ký tài khoản");

        String html = CommonUtil.buildOtpEmailHtml(
                null,           // chưa có tên → "Quý khách"
                otp,
                5,
                "Đăng ký tài khoản"
        );

        // 5. Gửi email HTML
        emailService.sendAppointmentEmail(email, subject, html);
    }

    // Xác thực OTP
    public void verifyOtp(String email, String otp) {

        EmailOtp emailOtp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP không tồn tại"));

        if (emailOtp.getExpireTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(emailOtp);
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!emailOtp.getOtp().equals(otp)) {
            throw new RuntimeException("OTP không đúng");
        }

        // OTP đúng → xóa
        otpRepository.delete(emailOtp);
    }
}
