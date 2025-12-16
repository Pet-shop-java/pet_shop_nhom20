package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.Payment;
import com.webpet_nhom20.backdend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByProviderRefAndStatus(String providerRef, PaymentStatus status);
}
