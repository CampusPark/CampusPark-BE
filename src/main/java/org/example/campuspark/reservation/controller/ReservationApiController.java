package org.example.campuspark.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.global.dto.ApiResponse;
import org.example.campuspark.reservation.dto.ReservationRequest;
import org.example.campuspark.reservation.dto.ReservationResponse;
import org.example.campuspark.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createReservation(
        @RequestParam("userId") Long userId,
        @Valid @RequestBody ReservationRequest request) {

        reservationService.createReservation(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("예약이 성공적으로 생성되었습니다."));
    }

//    @GetMapping("/{reservationId}")
//    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(
//        @PathVariable Long reservationId) {
//
//        ReservationResponse response = reservationService.getReservation(reservationId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservations(
//        @RequestParam("userId") Long userId) {
//
//        List<ReservationResponse> response = reservationService.getReservations(userId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    @DeleteMapping("/{reservationId}")
//    public ResponseEntity<ApiResponse<Void>> deleteReservation(
//        @PathVariable Long reservationId) {
//
//        reservationService.deleteReservation(reservationId);
//        return ResponseEntity.ok(ApiResponse.success("예약이 성공적으로 삭제되었습니다."));
//    }
}