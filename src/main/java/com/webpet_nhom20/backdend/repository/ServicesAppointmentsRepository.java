package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.ServiceAppointments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServicesAppointmentsRepository extends JpaRepository<ServiceAppointments,Integer> {
    // CUSTOMER: lấy theo userId, ưu tiên SCHEDULED, rồi sắp theo appoinmentStart tăng dần
    @Query("""
    SELECT s FROM ServiceAppointments s
    WHERE s.user.id = :userId
    ORDER BY
      CASE 
        WHEN s.status = 'SCHEDULED' AND s.appointmentStart >= CURRENT_TIMESTAMP THEN 0
        WHEN s.status = 'SCHEDULED' AND s.appointmentStart < CURRENT_TIMESTAMP THEN 1
        ELSE 2
      END,
      ABS(TIMESTAMPDIFF(SECOND, s.appointmentStart, CURRENT_TIMESTAMP)) ASC
""")
    Page<ServiceAppointments> findByUserIdOrderByStatusAndNearest(
            Integer userId,
            Pageable pageable
    );

    // ADMIN: lấy tất cả, ưu tiên SCHEDULED, rồi theo thời gian gần nhất
    @Query("""
    SELECT s FROM ServiceAppointments s
    ORDER BY
      CASE 
        WHEN s.status = 'SCHEDULED' AND s.appointmentStart >= CURRENT_TIMESTAMP THEN 0
        WHEN s.status = 'SCHEDULED' AND s.appointmentStart < CURRENT_TIMESTAMP THEN 1
        ELSE 2
      END,
      ABS(TIMESTAMPDIFF(SECOND, s.appointmentStart, CURRENT_TIMESTAMP)) ASC
""")
    Page<ServiceAppointments> findAllOrderByStatusAndNearest(Pageable pageable);
    // Check user book trùng giờ
    @Query("""
        SELECT COUNT(a) > 0
        FROM ServiceAppointments a
        WHERE a.user.id = :userId
          AND a.status <> 'CANCELLED'
          AND (
            a.appointmentStart < :end
            AND a.appointmentEnd > :start
          )
    """)
    boolean existsOverlappingAppointment(
            @Param("userId") int userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

