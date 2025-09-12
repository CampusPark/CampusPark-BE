package org.example.campuspark.parkingspace.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.global.exception.BusinessException;
import org.example.campuspark.global.exception.ErrorCode;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceRequestDto;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.parkingspace.repository.ParkingSpaceRepository;
import org.example.campuspark.user.domain.User;
import org.example.campuspark.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createParkingSpace(Long userId, ParkingSpaceRequestDto requestDto) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ParkingSpace parkingSpace = requestDto.toEntity(user);
        parkingSpaceRepository.save(parkingSpace);
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

    @Transactional
    public void deleteParkingSpace(Long userId, Long parkingSpaceId) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceById(parkingSpaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARKING_SPACE_NOT_FOUND));

        parkingSpaceRepository.delete(parkingSpace);
    }

}
