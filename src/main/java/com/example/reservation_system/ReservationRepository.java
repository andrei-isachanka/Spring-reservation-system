package com.example.reservation_system;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

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
