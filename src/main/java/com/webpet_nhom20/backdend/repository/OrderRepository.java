package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByUserId(Integer userId,
                                Pageable pageable);
    Optional<Order> findByOrderCode(String orderCode);
}
