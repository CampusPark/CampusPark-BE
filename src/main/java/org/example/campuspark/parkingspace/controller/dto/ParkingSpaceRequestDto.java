package org.example.campuspark.parkingspace.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.user.domain.User;

public record ParkingSpaceRequestDto(
    @NotNull(message = "주소는 필수입니다.")
    String address,

    @NotNull(message = "위도는 필수입니다.")
    Double latitude,

    @NotNull(message = "경도는 필수입니다.")
    Double longitude,

    @NotNull(message = "이용 가능 시작 시간은 필수입니다.")
    LocalDateTime availableStartTime,

    @NotNull(message = "이용 가능 종료 시간은 필수입니다.")
    LocalDateTime availableEndTime,

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    Integer price,

    @NotNull(message = "주차 가능 대수는 필수입니다.")
    @Positive(message = "주차 가능 대수는 1 이상이어야 합니다.")
    Integer availableCount
) {
    public ParkingSpace toEntity(User user) {
        return ParkingSpace.builder()
            .user(user)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .availableStartTime(availableStartTime)
            .availableEndTime(availableEndTime)
            .price(price)
            .status(true)
            .availableCount(availableCount)
            .build();
    }
}