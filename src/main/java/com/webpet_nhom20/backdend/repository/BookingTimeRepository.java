package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.BookingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
