package org.example.campuspark.ai.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.example.campuspark.ai.dto.ParseNumberRequest;
import org.example.campuspark.ai.dto.TimeRangeDto;
import org.example.campuspark.ai.service.NumberParsingService;
import org.example.campuspark.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final NumberParsingService numberParsingService;

    @PostMapping("/parse-number")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> parseNumber(@RequestBody ParseNumberRequest request) {
        Integer number = numberParsingService.parseNumberFromText(request.text());
        return ResponseEntity.ok(ApiResponse.success("Successfully parsed number", Map.of("number", number)));
    }

    @PostMapping("/parse-time")
    public ResponseEntity<ApiResponse<TimeRangeDto>> parseTime(@RequestBody ParseNumberRequest request) {
        CompletableFuture<TimeRangeDto> future = numberParsingService.parseTimeRange(request.text());
        TimeRangeDto timeRange = future.join(); // Wait for the async method to complete
        return ResponseEntity.ok(ApiResponse.success("Successfully parsed time range", timeRange));
    }
}
