package org.example.campuspark.stt.application;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.ai.dto.TimeRangeDto;
import org.example.campuspark.ai.service.NumberParsingService;
import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.service.GeoCodingService;
import org.example.campuspark.global.exception.BusinessException;
import org.example.campuspark.global.exception.ErrorCode;
import org.example.campuspark.global.redis.RedisService;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceDetailResponse;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.parkingspace.service.ParkingSpaceService;
import org.example.campuspark.reservation.dto.ReservationRequest;
import org.example.campuspark.reservation.dto.ReservationResponse;
import org.example.campuspark.reservation.service.ReservationService;
import org.example.campuspark.stt.controller.dto.TextParseRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SttService {
    private static final double DEFAULT_RADIUS = 10;
    private final GeoCodingService geoCodingService;
    private final ParkingSpaceService parkingSpaceService;
    private final NumberParsingService numberParsingService;
    private final RedisService redisService;
    private final ReservationService reservationService;

    /**
     * 주소를 기반으로 주변 주차 공간을 조회합니다.
     * @param userId
     * @param addressText
     * @return
     */
    public List<ParkingSpaceResponseDto> getParkingSpacesByAddress(Long userId, String addressText) {
        CoordinateResponse coordinate = geoCodingService.getCoordinatesByKeyword(addressText);

        return parkingSpaceService.storeNearbyParkingSpaces(userId, coordinate.latitude(), coordinate.longitude(), DEFAULT_RADIUS);
    }


    public ParkingSpaceDetailResponse getParkingSpaceDetails(Long userId, TextParseRequest request) {
        Integer number = numberParsingService.parseNumberFromText(request.text());
        if (number == null) {
            throw new BusinessException(ErrorCode.PARSING_ERROR);
        }

        List<Long> nearbySpaceIds = redisService.getNearbyParkingSpaceIds(userId);

        if (number <= 0 || number > nearbySpaceIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_PARKING_SPACE_SELECTION);
        }

        Long parkingSpaceId = ((Number) nearbySpaceIds.get(number - 1)).longValue();
        return parkingSpaceService.getParkingSpaceDetails(parkingSpaceId, LocalDate.now());
    }

    public ReservationResponse reserveParkingSpace(Long userId, Long parkingSpaceId, TextParseRequest request) {
        CompletableFuture<TimeRangeDto> future = numberParsingService.parseTimeRange(request.text());
        TimeRangeDto timeRange = future.join();

        log.info("Parsed time range: {} - {}", timeRange.startTime(), timeRange.endTime());
        ReservationRequest reservationRequest = new ReservationRequest(
                parkingSpaceId,
                timeRange.startTime(),
                timeRange.endTime()
        );

        return reservationService.createReservation(userId, reservationRequest);
    }
}
