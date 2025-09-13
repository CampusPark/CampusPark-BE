package org.example.campuspark.reservation.dto;

import org.example.campuspark.reservation.domain.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long userId,
    Long parkingSpaceId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getUser().getId(),
            reservation.getParkingSpace().getId(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getStatus().name()
        );
    }
}