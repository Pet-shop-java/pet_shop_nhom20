package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, Integer> {
    Optional<CartItems> findByCartIdAndProductVariantId(Integer cartId, Integer variantId);

    List<CartItems> findByCartId(Integer cartId);

    void deleteByCartId(Integer cartId);

    Optional<CartItems> findByIdAndCart_User_Id(Integer id, Integer userId);

    @Modifying
    @Query("""
    DELETE FROM CartItems ci
    WHERE ci.cart.id = :cartId
      AND ci.productVariant.id = :productVariantId
""")
    void deleteByCartIdAndProductVariantId(
            @Param("cartId") Integer cartId,
            @Param("productVariantId") Integer productVariantId
    );

}
