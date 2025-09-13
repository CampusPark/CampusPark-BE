package org.example.campuspark.parkingspace.controller.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.campuspark.parkingspace.domain.ParkingSpace;

public record ParkingSpaceResponseDto(
    Long id,
    String address,
    Double latitude,
    Double longitude,
    LocalTime availableStartTime,
    LocalTime availableEndTime,
    Integer price,
    Boolean status,
    int availableCount
) {
    public static ParkingSpaceResponseDto from(ParkingSpace parkingSpace) {
        return new ParkingSpaceResponseDto(
            parkingSpace.getId(),
            parkingSpace.getAddress(),
            parkingSpace.getLatitude(),
            parkingSpace.getLongitude(),
            parkingSpace.getAvailableStartTime(),
            parkingSpace.getAvailableEndTime(),
            parkingSpace.getPrice(),
            parkingSpace.getStatus(),
            parkingSpace.getAvailableCount()
        );
    }
}
