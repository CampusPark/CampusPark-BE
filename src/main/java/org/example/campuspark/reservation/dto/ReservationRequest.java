package org.example.campuspark.reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
    @NotNull Long parkingSpaceId,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime
) {}