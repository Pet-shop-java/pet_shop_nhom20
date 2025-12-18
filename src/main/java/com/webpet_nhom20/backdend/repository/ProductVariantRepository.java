package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.ProductVariants;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariants, Integer> {
    List<ProductVariants> findByProductId(int productId);
    boolean existsByVariantName(String name);
    boolean existsByProductIdAndVariantName(int productId, String variantName);
    Optional<ProductVariants> findByIdAndIsDeletedNot(Integer id, String isDeleted);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM ProductVariants v WHERE v.id = :id")
    Optional<ProductVariants> findByIdForUpdate(@Param("id") Long id);

}
