package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.dto.response.Product.BrandResponse;
import com.webpet_nhom20.backdend.entity.Pets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pets, Integer> {
    @Query("""
    SELECT COUNT(p) > 0
    FROM Pets p
    WHERE LOWER(p.name) = LOWER(:name)
    """)
    boolean existsByNameIgnoreCase(String name);
    boolean existsByName(String name);
    @Query("""
    SELECT p FROM Pets p
    WHERE (:animal IS NULL OR p.animal = :animal)
    AND (:size IS NULL OR p.size = :size)
    AND (:ageGroup IS NULL OR p.ageGroup = :ageGroup)
    AND (:isDelete IS NULL OR p.isDeleted = :isDelete)
    """)
    Page<Pets> findAllWithFilters(
            @Param("animal") String animal,
            @Param("size") String size,
            @Param("ageGroup") String ageGroup,
            @Param("isDelete") String isDelete,
            Pageable pageable
    );
    @Query(
            value = "SELECT DISTINCT animal FROM pets WHERE is_deleted = '0'",
            nativeQuery = true
    )
    List<String> getAnimalForCustomer();

    @Query(value = """
        select distinct animal from pets
    """, nativeQuery = true)
    List<String> getAnimalForAdmin();
}
