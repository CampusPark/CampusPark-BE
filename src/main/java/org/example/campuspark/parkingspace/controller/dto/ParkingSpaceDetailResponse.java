package org.example.campuspark.parkingspace.controller.dto;

import java.util.List;

public record ParkingSpaceDetailResponse(
    ParkingSpaceResponseDto parkingSpace,
    List<AvailableTimeSlotDto> availableTimeSlots
) {
}
