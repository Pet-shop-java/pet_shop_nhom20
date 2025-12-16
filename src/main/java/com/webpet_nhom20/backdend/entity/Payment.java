package com.webpet_nhom20.backdend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Lob
    @Column(name = "provider", nullable = false)
    private String provider;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Size(max = 10)
    @ColumnDefault("'VND'")
    @Column(name = "currency", length = 10)
    private String currency;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Size(max = 10)
    @Column(name = "response_code", length = 10)
    private String responseCode;

    @Size(max = 50)
    @Column(name = "transaction_no", length = 50)
    private String transactionNo;

    @Size(max = 20)
    @Column(name = "bank_code", length = 20)
    private String bankCode;



    @Size(max = 100)
    @Column(name = "provider_ref", length = 100)
    private String providerRef;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;



    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedDate;



    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        updatedDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }


}