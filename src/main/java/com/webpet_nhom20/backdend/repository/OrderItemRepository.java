package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.dto.response.OrderItem.OrderItemResponse;
import com.webpet_nhom20.backdend.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {
    List<OrderItemResponse> findByOrderId(Integer id);

    @Query(
            value = """
    SELECT *
    FROM order_items oi
    WHERE oi.order_id = :orderId
  """,
            nativeQuery = true
    )
    List<OrderItems> findByOrderIdQuery(
            @Param("orderId") Integer orderId
    );

}
