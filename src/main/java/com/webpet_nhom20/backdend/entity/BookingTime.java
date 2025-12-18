package com.webpet_nhom20.backdend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "booking_time")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    ServicesPet service;

    @Column(name = "slot_date", nullable = false)
    LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "max_capacity", nullable = false)
    Integer maxCapacity;

    @Column(name = "booked_count", nullable = false)
    Integer bookedCount = 0;

    @Column(name = "available_count", nullable = false)
    Integer availableCount;

    @Column(name = "is_active", length = 1)
    String isActive = "1";

    @Column(name = "create_date", updatable = false)
    LocalDateTime createDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.availableCount = this.maxCapacity - this.bookedCount;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
        this.availableCount = this.maxCapacity - this.bookedCount;
    }
}
