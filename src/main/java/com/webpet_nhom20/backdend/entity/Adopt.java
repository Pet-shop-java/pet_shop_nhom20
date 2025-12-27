package com.webpet_nhom20.backdend.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Entity
@Table(name = "adopt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adopt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "pet_id", nullable = false)
    private int petId;

    @Column(name = "address_id", nullable = false)
    private int addressId;
    @Column(name = "status", nullable = false )
    private String status;
    @Column(name="job")
    private String job;
    @Column(name="income")
    private String income;
    @Column(name="live_condition")
    private String liveCondition;
    @Column(name="is_own_pet")
    private String isOwnPet;
    @Column(columnDefinition = "TEXT")
    private String note;
    @Column(name ="code")
    private String code;
    @Column(name = "is_deleted")
    private String isDeleted;

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


