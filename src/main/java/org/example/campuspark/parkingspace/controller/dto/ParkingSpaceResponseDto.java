package org.example.campuspark.parkingspace.controller.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.parkingspace.domain.ParkingSpacePhoto;

public record ParkingSpaceResponseDto(
    Long id,
    String name,
    String address,
    Double latitude,
    Double longitude,
    LocalTime availableStartTime,
    LocalTime availableEndTime,
    Integer price,
    Boolean status,
    int availableCount,
    String thumbnailUrl,
    List<String> photoUrls
) {
    public static ParkingSpaceResponseDto from(ParkingSpace parkingSpace) {
        List<String> photoUrls = parkingSpace.getPhotos().stream()
                .map(ParkingSpacePhoto::getImageUrl)
                .collect(Collectors.toList());

        return new ParkingSpaceResponseDto(
            parkingSpace.getId(),
            parkingSpace.getName(),
            parkingSpace.getAddress(),
            parkingSpace.getLatitude(),
            parkingSpace.getLongitude(),
            parkingSpace.getAvailableStartTime(),
            parkingSpace.getAvailableEndTime(),
            parkingSpace.getPrice(),
            parkingSpace.getStatus(),
            parkingSpace.getAvailableCount(),
            parkingSpace.getThumbnailUrl(),
            photoUrls
        );
    }
}
