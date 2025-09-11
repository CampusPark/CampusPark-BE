package org.example.campuspark.geocoding.controller;

import lombok.RequiredArgsConstructor;
import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.service.GeoCodingService;
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
    public CoordinateResponse getCoordinates(@RequestParam String address) {
        return geoCodingService.getCoordinates(address);
    }
}