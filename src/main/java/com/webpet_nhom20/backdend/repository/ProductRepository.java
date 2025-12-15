package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.dto.response.Product.BrandResponse;
import com.webpet_nhom20.backdend.entity.Categories;
import com.webpet_nhom20.backdend.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products,Integer>, JpaSpecificationExecutor<Products> {
    boolean existsByName(String name);
    List<Products> findAllByCategoryId(int categoryId);

    Page<Products> findByCategoryId(int categoryId, Pageable pageable);
    // 1. Hàm lọc chung (Mặc định, Mới nhất, Bán chạy)
    // Dùng LEFT JOIN để lấy cả sản phẩm chưa có variant hoặc hết hàng
    // 1. Lọc mặc định (không sắp xếp giá cụ thể)
    @Query(value = "SELECT DISTINCT p.* FROM products p " +
            "LEFT JOIN product_variants v ON p.id = v.product_id " +
            "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:animal IS NULL OR p.animal = :animal) " + // Thêm lọc animal
            "AND (:brand IS NULL OR p.brand = :brand) " +
            "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
            "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +// Thêm lọc brand
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice)",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM products p " +
                    "LEFT JOIN product_variants v ON p.id = v.product_id " +
                    "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
                    "AND (:animal IS NULL OR p.animal = :animal) " + // Thêm lọc animal
                    "AND (:brand IS NULL OR p.brand = :brand) " +
                    "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
                    "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +// Thêm lọc brand
                    "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                    "AND (:maxPrice IS NULL OR v.price <= :maxPrice)",
            nativeQuery = true)
    Page<Products> findAllWithFilters(
            @Param("categoryId") Integer categoryId,
            @Param("animal") String animal, // Thêm tham số
            @Param("brand") String brand,
            @Param("is_deleted") String isDelete,
            @Param("is_featured") String isFeature,// Thêm tham số
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // 2. Sắp xếp giá TĂNG DẦN (ASC)
    @Query(value = "SELECT p.* FROM products p " +
            "LEFT JOIN product_variants v ON p.id = v.product_id " +
            "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:animal IS NULL OR p.animal = :animal) " +
            "AND (:brand IS NULL OR p.brand = :brand) " +
            "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
            "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "GROUP BY p.id " +
            "ORDER BY MIN(COALESCE(v.price, 999999999)) ASC",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM products p " +
                    "LEFT JOIN product_variants v ON p.id = v.product_id " +
                    "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
                    "AND (:animal IS NULL OR p.animal = :animal) " +
                    "AND (:brand IS NULL OR p.brand = :brand) " +
                    "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
                    "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +
                    "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                    "AND (:maxPrice IS NULL OR v.price <= :maxPrice)",
            nativeQuery = true)
    Page<Products> findAllWithFiltersSortedByPriceAsc(
            @Param("categoryId") Integer categoryId,
            @Param("animal") String animal,
            @Param("brand") String brand,
            @Param("is_deleted") String isDelete,
            @Param("is_featured") String isFeature,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // 3. Sắp xếp giá GIẢM DẦN (DESC)
    @Query(value = "SELECT p.* FROM products p " +
            "LEFT JOIN product_variants v ON p.id = v.product_id " +
            "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:animal IS NULL OR p.animal = :animal) " +
            "AND (:brand IS NULL OR p.brand = :brand) " +
            "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
            "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "GROUP BY p.id " +
            "ORDER BY MIN(COALESCE(v.price, 0)) DESC",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM products p " +
                    "LEFT JOIN product_variants v ON p.id = v.product_id " +
                    "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
                    "AND (:animal IS NULL OR p.animal = :animal) " +
                    "AND (:brand IS NULL OR p.brand = :brand) " +
                    "AND (:is_deleted IS NULL OR p.is_deleted = :is_deleted) " +
                    "AND (:is_featured IS NULL OR p.is_featured = :is_featured)" +
                    "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                    "AND (:maxPrice IS NULL OR v.price <= :maxPrice)",
            nativeQuery = true)
    Page<Products> findAllWithFiltersSortedByPriceDesc(
            @Param("categoryId") Integer categoryId,
            @Param("animal") String animal,
            @Param("brand") String brand,
            @Param("is_deleted") String isDelete,
            @Param("is_featured") String isFeature,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
    @Query("""
        SELECT new com.webpet_nhom20.backdend.dto.response.Product.BrandResponse(
            p.brand,
            COUNT(p.brand)
        )
        FROM Products p
        WHERE p.brand IS NOT NULL AND p.isDeleted = '0'
        GROUP BY p.brand
    """)
    List<BrandResponse> getBrand();


}
