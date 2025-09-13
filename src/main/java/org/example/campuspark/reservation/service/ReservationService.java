package org.example.campuspark.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.campuspark.reservation.dto.ReservationRequest;
import org.example.campuspark.reservation.dto.ReservationResponse;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.reservation.repository.ReservationRepository;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.parkingspace.repository.ParkingSpaceRepository;
import org.example.campuspark.user.domain.User;
import org.example.campuspark.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    @Transactional
    public void createReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(request.parkingSpaceId())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 주차공간 ID입니다."));

        Reservation reservation = Reservation.builder()
            .user(user)
            .parkingSpace(parkingSpace)
            .startTime(request.startTime())
            .endTime(request.endTime())
            .status(Reservation.ReservationStatus.RESERVED)
            .build();

        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        return ReservationResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream()
            .map(ReservationResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservationRepository.delete(reservation);
    }
}