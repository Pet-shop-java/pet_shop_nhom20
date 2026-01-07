package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImages, Integer> {
    List<ProductImages> findByProductId(int productId);

    // Tìm ảnh primary chưa bị xóa
    @Query("SELECT pi FROM ProductImages pi WHERE pi.product.id = :productId AND pi.isPrimary = :isPrimary AND pi.isDeleted = '0'")
    Optional<ProductImages> findByProductIdAndIsPrimary(@Param("productId") int productId, @Param("isPrimary") int isPrimary);


    List<ProductImages> findByProductIdAndIsDeleted(Integer productId, String isDeleted);

    // Lấy ảnh primary của product (chưa xóa)
    @Query("""
        SELECT pi
        FROM ProductImages pi
        WHERE pi.product.id = :productId
          AND pi.isPrimary = 1
          AND pi.isDeleted = '0'
    """)
    Optional<ProductImages> findPrimaryImageByProductId(@Param("productId") Integer productId);


    // ================= VARIANT IMAGES =================

    // Lấy ảnh theo variant (chưa xóa)
    @Query("""
        SELECT pi
        FROM ProductImages pi
        JOIN ProductVariantImage pvi ON pi.id = pvi.image.id
        WHERE pvi.variant.id = :variantId
          AND pi.isDeleted = '0'
    """)
    List<ProductImages> findImagesByVariantId(@Param("variantId") Integer variantId);

}
