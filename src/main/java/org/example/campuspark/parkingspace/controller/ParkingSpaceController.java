package org.example.campuspark.parkingspace.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.global.dto.ApiResponse;
import org.example.campuspark.parkingspace.controller.dto.AvailableTimeSlotDto;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceRequestDto;
import org.example.campuspark.parkingspace.controller.dto.ParkingSpaceResponseDto;
import org.example.campuspark.parkingspace.service.ParkingSpaceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/parking-spaces")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createParkingSpace(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody ParkingSpaceRequestDto requestDto) {

        parkingSpaceService.createParkingSpace(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("주차공간이 성공적으로 등록되었습니다."));
    }

    @GetMapping("/{parkingSpaceId}")
    public ResponseEntity<ApiResponse<ParkingSpaceResponseDto>> getParkingSpace(
            @PathVariable Long parkingSpaceId) {
        ParkingSpaceResponseDto response = parkingSpaceService.getParkingSpace(parkingSpaceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
//
//    @GetMapping("/my")
//    public ResponseEntity<ApiResponse<Page<ParkingSpaceResponseDto>>> getMyParkingSpaces(
//            @RequestParam("userId") Long userId,
//            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        Page<ParkingSpaceResponseDto> response = parkingSpaceService.getUserParkingSpaces(userId, pageable);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ParkingSpaceResponseDto>>> getNearbyParkingSpaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {

        List<ParkingSpaceResponseDto> response = parkingSpaceService.getNearbyParkingSpaces(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/nearby/redis")
    public ResponseEntity<ApiResponse<List<ParkingSpaceResponseDto>>> getNearbyParkingSpaces(
            @RequestParam Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {

        List<ParkingSpaceResponseDto> response = parkingSpaceService.storeNearbyParkingSpaces(userId, latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{parkingSpaceId}/availability")
    public ResponseEntity<ApiResponse<List<AvailableTimeSlotDto>>> getAvailableTimeSlots(
            @PathVariable Long parkingSpaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableTimeSlotDto> response = parkingSpaceService.getAvailableTimeSlots(parkingSpaceId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{parkingSpaceId}")
    public ResponseEntity<ApiResponse<Void>> deleteParkingSpace(
            @RequestParam("userId") Long userId,
            @PathVariable Long parkingSpaceId) {

        parkingSpaceService.deleteParkingSpace(userId, parkingSpaceId);
        return ResponseEntity.ok(ApiResponse.success("주차공간이 성공적으로 삭제되었습니다.", null));
    }
}

