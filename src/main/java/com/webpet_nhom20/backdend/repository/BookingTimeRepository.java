package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.BookingTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingTimeRepository extends JpaRepository<BookingTime, Integer> {
    @Query("""
        SELECT MAX(b.slotDate)
        FROM BookingTime b
        WHERE b.service.id = :serviceId
    """)
    LocalDate findMaxSlotDateByService(int serviceId);

    @Query("""
        SELECT b FROM BookingTime b
        WHERE b.service.id = :serviceId
          AND b.slotDate = (
              SELECT MIN(bt.slotDate)
              FROM BookingTime bt
              WHERE bt.service.id = :serviceId
          )
        ORDER BY b.startTime
    """)
    List<BookingTime> findTemplateByService(int serviceId);
    List<BookingTime> findByServiceId(int serviceId);

    // Có lock để tránh 2 người book cùng lúc
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookingTime b WHERE b.id = :id")
    Optional<BookingTime> findByIdForUpdate(@Param("id") Integer id);

    List<BookingTime> findByServiceIdAndSlotDateAndIsActive(
            int service_id, LocalDate slotDate, String isActive
    );
}
