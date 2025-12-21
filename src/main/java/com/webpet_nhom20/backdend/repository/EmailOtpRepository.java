package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Integer> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);

    void deleteByExpireTimeBefore(LocalDateTime time);
}
