package org.example.campuspark.geocoding.controller;

import lombok.RequiredArgsConstructor;
import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.service.GeoCodingService;
import org.example.campuspark.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/geocode")
public class GeoCodingController {

    private final GeoCodingService geoCodingService;

    @GetMapping
    public ResponseEntity<ApiResponse<CoordinateResponse>> getCoordinates(@RequestParam String address) {
        CoordinateResponse response = geoCodingService.getCoordinates(address);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}