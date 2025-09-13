package org.example.campuspark.geocoding.service;

import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.dto.KakaoApiResponse;
import org.example.campuspark.geocoding.dto.KakaoKeywordResponse;
import org.example.campuspark.global.exception.CustomException;
import org.example.campuspark.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeoCodingService {

    private final WebClient webClient;
    private final String kakaoApiKey;

    public GeoCodingService(
        WebClient.Builder webClientBuilder,
        @Value("${kakao.api.key}") String kakaoApiKey
    ) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
        this.kakaoApiKey = kakaoApiKey;
    }

    // 장소명(키워드) -> 위도/경도 변환
    public CoordinateResponse getCoordinatesByKeyword(String keyword) {
        KakaoKeywordResponse kakaoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", keyword)
                        .queryParam("size", 1) // 첫 번째 결과만 가져옴
                        .build())
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(KakaoKeywordResponse.class)
                .block();

        if (kakaoResponse == null || kakaoResponse.documents() == null
                || kakaoResponse.documents().isEmpty()) {
            throw CustomException.from(ErrorCode.ERROR_IN_GEOCODING);
        }

        KakaoKeywordResponse.Document doc = kakaoResponse.documents().get(0);
        double longitude = Double.parseDouble(doc.x());
        double latitude = Double.parseDouble(doc.y());

        return CoordinateResponse.of(latitude, longitude);
    }
}
