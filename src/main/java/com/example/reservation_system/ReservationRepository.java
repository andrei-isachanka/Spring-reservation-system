package com.example.reservation_system;

import jakarta.persistence.LockModeType;
import org.hibernate.LockMode;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT r FROM ReservationEntity r
       WHERE r.roomId = :roomId
         AND r.startDate <= :endDate
         AND r.endDate >= :startDate
         AND r.id <> :excludeId
         AND r.status <> 'CANCELLED'
       """)
    List<ReservationEntity> findAndLockConflict(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );


    @Modifying
    @Query("update ReservationEntity r set r.status = :status where r.id = :id")
    void setStatus(
            @Param("id") Long id,
            @Param("status") ReservationStatus reservationStatus);


    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservationEntity r WHERE " +
            "r.roomId = :roomId AND r.status = 'APPROVED' AND r.id != :excludeReservationId AND " +
            "r.startDate < :endDate AND r.endDate > :startDate")
    boolean existsConflictingReservation(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeReservationId") Long excludeReservationId
    );
}
