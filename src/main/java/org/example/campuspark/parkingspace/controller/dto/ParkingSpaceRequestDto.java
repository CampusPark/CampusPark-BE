package org.example.campuspark.parkingspace.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.example.campuspark.user.domain.User;
import org.springframework.cglib.core.Local;

public record ParkingSpaceRequestDto(
    @NotNull(message = "주소는 필수입니다.")
    String address,

    @NotNull(message = "건물의 이름을 알려주세요")
    String name,

    @NotNull(message = "위도는 필수입니다.")
    Double latitude,

    @NotNull(message = "경도는 필수입니다.")
    Double longitude,

    @NotNull(message = "이용 가능 시작 시간은 필수입니다.")
    LocalTime availableStartTime,

    @NotNull(message = "이용 가능 종료 시간은 필수입니다.")
    LocalTime availableEndTime,

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    Integer price,

    @NotNull(message = "주차 가능 대수는 필수입니다.")
    @Positive(message = "주차 가능 대수는 1 이상이어야 합니다.")
    Integer availableCount,

    @Size(max = 4, message = "사진은 최대 4개까지 등록할 수 있습니다.")
    List<String> photoUrls,

    String thumbnailUrl

) {
    public ParkingSpace toEntity(User user) {
        return ParkingSpace.builder()
            .user(user)
            .name(name)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .availableStartTime(availableStartTime)
            .availableEndTime(availableEndTime)
            .price(price)
            .status(true)
            .availableCount(availableCount)
            .thumbnailUrl(thumbnailUrl)
            .build();
    }
}