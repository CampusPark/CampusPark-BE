package org.example.campuspark.parkingspace.controller.dto;

import java.time.LocalDateTime;

public record AvailableTimeSlotDto(
    LocalDateTime startTime,
    LocalDateTime endTime
) {}
