package org.example.campuspark.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campuspark.ai.dto.TimeRangeDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NumberParsingService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Integer parseNumberFromText(String text) {
        String prompt = """
                You are a helpful assistant that extracts numbers from text.
                Analyze the following text and identify the number it represents.
                The number can be a digit, a word (e.g., '세번째', '첫번째'), or a mix (e.g., '3번째').
                Return the number as a single digit.
                
                Your response must be a JSON object with a single key "number".
                
                Example 1:
                Input: "세번째 주차공간을 선택해줘"
                Output: {"number": 3}
                
                Example 2:
                Input: "1번째"
                Output: {"number": 1}
                
                Now, analyze this text:
                Input: "%s"
                """.formatted(text);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        String jsonResponse = cleanResponse(response);

        try {
            Map<String, Integer> result = objectMapper.readValue(jsonResponse, Map.class);
            return result.get("number");
        } catch (Exception e) {
            log.error("Failed to parse number from AI response: {}", jsonResponse, e);
            // Handle error, maybe return null or throw a custom exception
            return null;
        }
    }

    @Async
    public CompletableFuture<TimeRangeDto> parseTimeRange(String text) {
        String prompt = """
                You are a helpful assistant that extracts a time range from text.
                Analyze the following text and identify the start time and end time.
                The user is asking this at "%s". Use this as the current time for any relative calculations (e.g., 'today', 'tomorrow', 'in 2 hours').
                
                Your response MUST be a JSON object with two keys: "startTime" and "endTime".
                The format for the time values MUST be ISO 8601 (e.g., "yyyy-MM-dd'T'HH:mm:ss").
                
                Example 1:
                Input: "오늘 오후 2시부터 4시까지 예약하고 싶어"
                (Assuming today is 2025-09-13)
                Output: {"startTime": "2025-09-13T14:00:00", "endTime": "2025-09-13T16:00:00"}
                
                Example 2:
                Input: "내일 10시부터 11시 30분"
                (Assuming today is 2025-09-13)
                Output: {"startTime": "2025-09-14T10:00:00", "endTime": "2025-09-14T11:30:00"}
                
                Now, analyze this text based on the current time provided above:
                Input: "%s"
                """.formatted(LocalDateTime.now().toString(), text);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        String jsonResponse = cleanResponse(response);

        try {
            TimeRangeDto timeRange = objectMapper.readValue(jsonResponse, TimeRangeDto.class);
            return CompletableFuture.completedFuture(timeRange);
        } catch (Exception e) {
            log.error("Failed to parse time range from AI response: {}", jsonResponse, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private String cleanResponse(String response) {
        if (response.startsWith("```json")) {
            return response.substring(7, response.length() - 3).trim();
        }
        return response;
    }
}
