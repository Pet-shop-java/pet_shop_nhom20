package com.webpet_nhom20.backdend.entity;


import com.webpet_nhom20.backdend.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "order_code" , nullable = false)
    String orderCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private Set<OrderItems> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "payment_method",nullable =false )
    private String paymentMethod;

    @Column(name = "total_amount" , nullable = false)
    BigDecimal totalAmount;

    @Column(name = "payment_expired_at")
    private LocalDateTime paymentExpiredAt;

    @Column(name = "shipping_amount" , nullable = false)
    BigDecimal shippingAmount;

    @Column(
            name = "final_amount",
            insertable = false,
            updatable = false
    )
    BigDecimal finalAmount;

    @Column(name = "discount_amount" )
    Float discountPercent;

    @Column(name = "shipping_address" , nullable = false , length = 500)
    String shippingAddress ;

    @Column(name = "status" , nullable = false)
    String status;

    @Column(name = "note" , length = 500)
    String note;

    @Column(name = "is_deleted" , length = 1)
    String isDeleted = "0";

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    Date createdDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    Date updatedDate;


    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        updatedDate = new Date();
    }

    /**
     * Tự động update ngày khi cập nhật
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }

}
