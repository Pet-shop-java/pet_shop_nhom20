package com.webpet_nhom20.backdend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "pet_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading để tối ưu hiệu năng
    @JoinColumn(name = "pet_id")
    @ToString.Exclude
    private Pets pet;
    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "image_url", nullable = false)
    String imageUrl;

    @Column(name = "image_position")
    int imagePosition = 0;

    @Column(name = "is_primary")
    int isPrimary = 0;

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

