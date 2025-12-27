package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.PetImages;
import com.webpet_nhom20.backdend.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetImageRepository extends JpaRepository<PetImages, Integer> {
    List<PetImages> findByPetId(int petId);

//    // Tìm ảnh primary chưa bị xóa
//    @Query("SELECT pi FROM PetImages pi WHERE pi.pets.id = :petId AND pi.isPrimary = :isPrimary AND pi.isDeleted = '0'")
//    Optional<PetImages> findByPetIdAndIsPrimary(@Param("petId") int petId, @Param("isPrimary") int isPrimary);

}
