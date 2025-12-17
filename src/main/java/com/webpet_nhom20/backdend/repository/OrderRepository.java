package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.dto.response.Order.OrderDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.entity.Order;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.repository.projection.OrderDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByUserId(Integer userId,
                                Pageable pageable);
    Optional<Order> findByOrderCode(String orderCode);
    Page<Order> findAllByUserIdAndStatus(Integer userId, String status,
                                         Pageable pageable);
    @Query(value = """
        SELECT 
            o.id AS orderPrimaryId,
            o.order_code AS orderCode,
            o.status AS status,
            o.total_amount AS totalAmount,
            o.shipping_address AS shippingAddress,
            o.created_date AS orderDate,

            oi.id AS orderItemId,
            oi.quantity AS quantity,
            oi.unit_price AS unitPrice,
            oi.total_price AS totalPrice,

            p.name AS productName,

            pv.id AS variantId,
            pv.price AS price,
            pv.stock_quantity AS stockQuantity,

            pi.id AS imageId,
            pi.image_url AS imageUrl

        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN product_variants pv ON oi.product_variant_id = pv.id
        JOIN products p ON pv.product_id = p.id
        LEFT JOIN product_variant_image pvi ON pv.id = pvi.variant_id
        LEFT JOIN product_images pi ON pvi.image_id = pi.id
        WHERE o.id = :orderId
    """, nativeQuery = true)
    List<OrderDetailProjection> getOrderDetailsNative(@Param("orderId") Integer orderId);
}
