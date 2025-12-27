package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptDetailResponse;
import com.webpet_nhom20.backdend.entity.Adopt;
import com.webpet_nhom20.backdend.repository.projection.AdoptDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AdoptRepository extends JpaRepository<Adopt, Integer> {
    boolean existsByUserIdAndPetIdAndStatusAndIsDeletedFalse(
            int userId,
            int petId,
            String status
    );
    @Query(
            value = """
        SELECT a.*
        FROM adopt a
        WHERE a.user_id = :userId
          AND (:code IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
          AND (:status IS NULL OR a.status = :status)
          AND (:isDeleted IS NULL OR a.is_deleted = :isDeleted)
        ORDER BY a.created_date DESC
    """,
            countQuery = """
        SELECT COUNT(a.id)
        FROM adopt a
        WHERE a.user_id = :userId
          AND (:code IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
          AND (:status IS NULL OR a.status = :status)
          AND (:isDeleted IS NULL OR a.is_deleted = :isDeleted)
    """,
            nativeQuery = true
    )
    Page<Adopt> findAdoptsByUserWithFilters(
            @Param("userId") int userId,
            @Param("code") String code,
            @Param("status") String status,
            @Param("isDeleted") String isDeleted,
            Pageable pageable
    );
    @Query("""
SELECT
 a.id          AS adoptId,
 a.code        AS code,
 a.status      AS status,
 a.note        AS note,
 a.job         AS job,
 a.income      AS income,
 a.isOwnPet    AS isOwnPet,
 a.liveCondition AS liveCondition,
 a.createdDate AS createdDate,

 u.id          AS userId,
 u.fullName    AS fullName,
 u.phone       AS phone,

 p.id          AS petId,
 p.name        AS petName,
 p.animal      AS animal,
 p.breed       AS breed,
 p.age         AS age,
 p.size        AS size,
 p.gender      AS gender,

 pi.imageUrl   AS petImage,

 CONCAT(ad.detailAddress, ', ', ad.ward, ', ', ad.city, ', ', ad.state) AS address

FROM Adopt a
JOIN User u ON u.id = a.userId
JOIN Pets p ON p.id = a.petId
LEFT JOIN PetImages pi ON pi.pet.id = p.id AND pi.isPrimary = 1 AND pi.isDeleted = '0'
LEFT JOIN Addresses ad ON ad.id = a.addressId AND ad.isDeleted = '0'

WHERE a.id = :adoptId AND a.isDeleted = '0'
""")
    Optional<AdoptDetailProjection> findAdoptDetail(@Param("adoptId") Integer adoptId);
    int countByPetIdAndStatusAndIsDeleted(
            int petId,
            String status,
            String isDeleted
    );
    @Query(
            value = """
        SELECT a.*
        FROM adopt a
        WHERE (:petId IS NULL OR a.pet_id = :petId)
          AND (:code IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
          AND (:status IS NULL OR a.status = :status)
          AND (:isDeleted IS NULL OR a.is_deleted = :isDeleted)
        ORDER BY a.created_date DESC
    """,
            countQuery = """
        SELECT COUNT(a.id)
        FROM adopt a
        WHERE (:petId IS NULL OR a.pet_id = :petId)
          AND (:code IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
          AND (:status IS NULL OR a.status = :status)
          AND (:isDeleted IS NULL OR a.is_deleted = :isDeleted)
    """,
            nativeQuery = true
    )
    Page<Adopt> findAllAdoptsForAdmin(
            @Param("petId") Integer petId,
            @Param("code") String code,
            @Param("status") String status,
            @Param("isDeleted") String isDeleted,
            Pageable pageable
    );
    @Modifying
    @Transactional
    @Query("""
        UPDATE Adopt a
        SET a.status = 'REJECTED'
        WHERE a.petId = :petId
        AND a.id <> :adoptId
    """)
    void cancelOtherAdopts(@Param("petId") Integer petId,
                           @Param("adoptId") Integer adoptId);





}
