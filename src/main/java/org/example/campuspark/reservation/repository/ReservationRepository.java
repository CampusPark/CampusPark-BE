package org.example.campuspark.reservation.repository;

import java.util.List;

import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.reservation.domain.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByParkingSpaceAndStatusIn(ParkingSpace parkingSpace, List<ReservationStatus> statuses);
}
