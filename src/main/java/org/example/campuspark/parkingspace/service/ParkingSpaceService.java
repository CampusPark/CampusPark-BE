package org.example.campuspark.parkingspace.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.global.exception.BusinessException;
import org.example.campuspark.global.exception.ErrorCode;
import org.example.campuspark.parkingspace.controller.dto.AvailableTimeSlotDto;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceRequestDto;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.parkingspace.repository.ParkingSpaceRepository;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.reservation.domain.Reservation.ReservationStatus;
import org.example.campuspark.reservation.repository.ReservationRepository;
import org.example.campuspark.user.domain.User;
import org.example.campuspark.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void createParkingSpace(Long userId, ParkingSpaceRequestDto requestDto) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ParkingSpace parkingSpace = requestDto.toEntity(user);
        parkingSpaceRepository.save(parkingSpace);
    }

    @Transactional
    public List<ParkingSpaceResponseDto> storeNearbyParkingSpaces(Long userId, Double latitude, Double longitude, Double radiusKm) {
        List<ParkingSpace> nearbySpaces = parkingSpaceRepository.findNearbyParkingSpaces(latitude, longitude, radiusKm);

        List<ParkingSpace> limitedNearbySpaces = nearbySpaces.stream()
                .limit(10)
                .toList();

        List<Long> nearbySpaceIds = limitedNearbySpaces.stream()
                .map(ParkingSpace::getId)
                .toList();

        cacheNearbyParkingSpaceIds(userId, nearbySpaceIds);

        return limitedNearbySpaces.stream()
                .map(ParkingSpaceResponseDto::from)
                .toList();
    }

    private void cacheNearbyParkingSpaceIds(Long userId, List<Long> spaceIds) {
        String redisKey = "nearby_parks:" + userId;
        redisTemplate.opsForValue().set(redisKey, spaceIds, 30, TimeUnit.SECONDS);
    }

    public ParkingSpaceResponseDto getParkingSpace(Long parkingSpaceId) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceById(parkingSpaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARKING_SPACE_NOT_FOUND));
        return ParkingSpaceResponseDto.from(parkingSpace);
    }

    public List<ParkingSpaceResponseDto> getNearbyParkingSpaces(Double latitude, Double longitude, Double radiusKm) {
        return parkingSpaceRepository.findNearbyParkingSpaces(latitude, longitude, radiusKm).stream()
            .map(ParkingSpaceResponseDto::from)
            .toList();
    }

    public List<AvailableTimeSlotDto> getAvailableTimeSlots(Long parkingSpaceId, LocalDate date) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(parkingSpaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARKING_SPACE_NOT_FOUND));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        LocalDateTime availableStart = parkingSpace.getAvailableStartTime();
        LocalDateTime availableEnd = parkingSpace.getAvailableEndTime();

        // Check if the parking space is available at all on the given date
        if (availableEnd.toLocalDate().isBefore(date) || availableStart.toLocalDate().isAfter(date)) {
            return Collections.emptyList();
        }

        LocalDateTime searchStart = availableStart.toLocalDate().isEqual(date) ? availableStart : dayStart;
        LocalDateTime searchEnd = availableEnd.toLocalDate().isEqual(date) ? availableEnd : dayEnd;


        List<ReservationStatus> statuses = List.of(ReservationStatus.RESERVED, ReservationStatus.BEING_USED);
        List<Reservation> reservations = reservationRepository.findReservationsForDate(
                parkingSpace, statuses, searchStart, searchEnd);

        List<AvailableTimeSlotDto> availableSlots = new ArrayList<>();
        LocalDateTime lastCheckedTime = searchStart;

        for (Reservation reservation : reservations) {
            if (reservation.getStartTime().isAfter(lastCheckedTime)) {
                availableSlots.add(new AvailableTimeSlotDto(lastCheckedTime, reservation.getStartTime()));
            }
            lastCheckedTime = reservation.getEndTime();
        }

        if (lastCheckedTime.isBefore(searchEnd)) {
            availableSlots.add(new AvailableTimeSlotDto(lastCheckedTime, searchEnd));
        }

        return availableSlots;
    }

    @Transactional
    public void deleteParkingSpace(Long userId, Long parkingSpaceId) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceById(parkingSpaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARKING_SPACE_NOT_FOUND));

        parkingSpaceRepository.delete(parkingSpace);
    }

}
