package org.example.campuspark.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.campuspark.global.exception.BusinessException;
import org.example.campuspark.global.exception.ErrorCode;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.parkingspace.repository.ParkingSpaceRepository;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.reservation.domain.Reservation.ReservationStatus;
import org.example.campuspark.reservation.dto.ReservationRequest;
import org.example.campuspark.reservation.dto.ReservationResponse;
import org.example.campuspark.reservation.repository.ReservationRepository;
import org.example.campuspark.user.domain.User;
import org.example.campuspark.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ParkingSpace parkingSpace = parkingSpaceRepository.findById(request.parkingSpaceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PARKING_SPACE_NOT_FOUND));

        if (request.startTime().toLocalTime().isBefore(parkingSpace.getAvailableStartTime()) ||
                request.endTime().toLocalTime().isAfter(parkingSpace.getAvailableEndTime())) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME);
        }

        List<ReservationStatus> statusesToCheck = List.of(ReservationStatus.RESERVED, ReservationStatus.BEING_USED);
        boolean isOverlapping = reservationRepository.existsOverlappingReservation(
                parkingSpace,
                request.startTime(),
                request.endTime(),
                statusesToCheck
        );

        if (isOverlapping) {
            throw new BusinessException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .parkingSpace(parkingSpace)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(ReservationStatus.RESERVED)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }
}
