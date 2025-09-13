package org.example.campuspark.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.reservation.domain.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);
    boolean existsByParkingSpaceAndStatusIn(ParkingSpace parkingSpace, List<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.parkingSpace = :parkingSpace " +
           "AND r.status IN :statuses " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    boolean existsOverlappingReservation(@Param("parkingSpace") ParkingSpace parkingSpace,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("statuses") List<ReservationStatus> statuses);

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.parkingSpace = :parkingSpace " +
           "AND r.status IN :statuses " +
           "AND r.startTime >= :start AND r.startTime < :end " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findReservationsForDate(
            @Param("parkingSpace") ParkingSpace parkingSpace,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
