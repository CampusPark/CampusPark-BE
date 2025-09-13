package org.example.campuspark.stt.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.service.GeoCodingService;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceDetailResponse;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.parkingspace.service.ParkingSpaceService;
import org.example.campuspark.stt.controller.dto.TextParseRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SttService {
    private static final double DEFAULT_RADIUS = 10;
    private final GeoCodingService geoCodingService;
    private final ParkingSpaceService parkingSpaceService;

    /**
     * 주소를 기반으로 주변 주차 공간을 조회합니다.
     * @param userId
     * @param addressText
     * @return
     */
    public List<ParkingSpaceResponseDto> getParkingSpacesByAddress(Long userId, String addressText) {
        CoordinateResponse coordinate = geoCodingService.getCoordinates(addressText);

        return parkingSpaceService.storeNearbyParkingSpaces(userId, coordinate.latitude(), coordinate.longitude(), DEFAULT_RADIUS);
    }


//    public List<ParkingSpaceDetailResponse> getParkingSpaceDetails(Long userId, TextParseRequest request) {
//
//    }
}
