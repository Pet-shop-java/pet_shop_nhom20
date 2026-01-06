package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.PetImages;
import com.webpet_nhom20.backdend.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetImageRepository extends JpaRepository<PetImages, Integer> {
    List<PetImages> findByPetId(int petId);
    @Modifying
    @Query("UPDATE PetImages p SET p.isDeleted = '1' WHERE p.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Integer> ids);

    @Modifying
    @Query("UPDATE PetImages p SET p.isPrimary = 0 WHERE p.pet.id = :petId")
    void resetPrimaryByPetId(@Param("petId") Integer petId);
    @Modifying
    @Query("""
        UPDATE PetImages pi
        SET pi.isDeleted = '1'
        WHERE pi.id IN :ids
    """)
    void softDeleteImages(@Param("ids") List<Integer> ids);

    List<PetImages> findByPetIdAndIsDeleted(Integer petId, String isDeleted);
//    // Tìm ảnh primary chưa bị xóa
//    @Query("SELECT pi FROM PetImages pi WHERE pi.pets.id = :petId AND pi.isPrimary = :isPrimary AND pi.isDeleted = '0'")
//    Optional<PetImages> findByPetIdAndIsPrimary(@Param("petId") int petId, @Param("isPrimary") int isPrimary);

}
