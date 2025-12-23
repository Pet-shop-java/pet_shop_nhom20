package com.webpet_nhom20.backdend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "animal", nullable = false, length = 100)
    private String animal; // Dog, Cat

    @Column(name = "breed", length = 100)
    private String breed;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "age_group", length = 100)
    private String ageGroup;

    @Column(name = "size", length = 100)
    private String size;

    @Column(name = "gender", length = 100)
    private String gender;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "health_status", length = 100)
    private String healthStatus;

    @Column(name = "vaccinated")
    private String vaccinated;

    @Column(name = "neutered")
    private String neutered;

    @Column(name = "is_deleted", length = 1)
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
