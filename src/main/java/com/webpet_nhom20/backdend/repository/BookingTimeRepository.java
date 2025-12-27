package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.BookingTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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
          AND b.isActive = '1'
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

    List<BookingTime> findByServiceIdAndSlotDateAndIsActiveAndIsDeleted(
            int service_id, LocalDate slotDate, String isActive, String isDeleted
    );

    /**
     * Lấy tất cả booking_time đang active
     * theo service + startTime + khoảng ngày
     */
    @Query("""
        SELECT bt
        FROM BookingTime bt
        WHERE bt.service.id = :serviceId
          AND bt.startTime = :startTime
          AND bt.slotDate BETWEEN :startDate AND :endDate
          AND bt.isActive = '1'
    """)
    List<BookingTime> findActiveByServiceAndTimeRange(
            @Param("serviceId") int serviceId,
            @Param("startTime") LocalTime startTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Check xem slot mới đã tồn tại hay chưa
     * (không phân biệt active hay inactive)
     */
    boolean existsByService_IdAndSlotDateAndStartTimeAndIsActiveTrue(
            int serviceId,
            LocalDate slotDate,
            LocalTime startTime
    );

    List<BookingTime> findByService_IdAndIsActive(int serviceId, String isActive);

    // Lấy toàn bộ slot ACTIVE theo service + giờ cũ
    List<BookingTime> findByService_IdAndStartTimeAndIsActive(
            int serviceId,
            LocalTime startTime,
            String isActive
    );

    List<BookingTime> findByService_IdAndStartTime(
            int serviceId,
            LocalTime startTime
    );

    boolean existsByService_IdAndStartTimeAndIsActive(
            int serviceId,
            LocalTime startTime,
            String isActive
    );
}
