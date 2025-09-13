package org.example.campuspark.stt.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.campuspark.global.dto.ApiResponse;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceDetailResponse;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.reservation.dto.ReservationResponse;
import org.example.campuspark.stt.application.SttService;
import org.example.campuspark.stt.controller.dto.AddressRequest;
import org.example.campuspark.stt.controller.dto.TextParseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stt")
@RequiredArgsConstructor
public class SttController {

    private final SttService sttService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ParkingSpaceResponseDto>>> getNearbyParkingSpaces(
            @RequestParam Long userId,
            @RequestBody AddressRequest request
    ) {
        List<ParkingSpaceResponseDto> response = sttService.getParkingSpacesByAddress(userId, request.address());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/detail")
    public ResponseEntity<ApiResponse<ParkingSpaceDetailResponse>> getParkingSpaceDetails(
            @RequestParam Long userId,
            @RequestBody TextParseRequest request
    ) {
        ParkingSpaceDetailResponse response = sttService.getParkingSpaceDetails(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveParkingSpace(
            @RequestParam Long userId,
            @RequestParam Long parkingSpaceId,
            @RequestBody TextParseRequest request
    ) {
        ReservationResponse response = sttService.reserveParkingSpace(userId, parkingSpaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("예약이 완료되었습니다.", response));
    }

}
