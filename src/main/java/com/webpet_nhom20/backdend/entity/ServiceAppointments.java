package com.webpet_nhom20.backdend.entity;

import com.webpet_nhom20.backdend.enums.AppoinmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceAppointments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServicesPet service;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column( name = "name_pet", nullable = false)
    String namePet;

    @Column( name = "specie_pet", nullable = false)
    String speciePet;

    @Column( name = "appointment_start", nullable = false)
    LocalDateTime appointmentStart;

    @Column( name = "appointment_end", nullable = false)
    LocalDateTime appointmentEnd;

    @Enumerated(EnumType.STRING)
    @Column( name = "status", nullable = false, length = 20)
    AppoinmentStatus status = AppoinmentStatus.SCHEDULED;

    @Column( name = "notes", columnDefinition = "TEXT")
    String notes;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
