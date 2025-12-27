package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.Categories;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesPetRepository extends JpaRepository<ServicesPet,Integer>, JpaSpecificationExecutor<ServicesPet> {
    List<ServicesPet> findByIsActive(String isActive);
    Page<ServicesPet> findByTitleContainingIgnoreCase(String name , Pageable pageable);
    List<ServicesPet> findByTitle(String title);
    boolean existsByName(String name);

    @Query("""
    SELECT s FROM ServicesPet s
    ORDER BY
        CASE WHEN s.isActive = '1' THEN 0 ELSE 1 END,
        s.createDate DESC
""")
    Page<ServicesPet> findAllOrderByActiveAndCreated(Pageable pageable);

    @Query("""
    SELECT s FROM ServicesPet s
    WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY
        CASE WHEN s.isActive = '1' THEN 0 ELSE 1 END,
        s.createDate DESC
""")
    Page<ServicesPet> searchOrderByActiveAndCreated(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
