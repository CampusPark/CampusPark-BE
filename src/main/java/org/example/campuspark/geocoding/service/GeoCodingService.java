package org.example.campuspark.geocoding.service;

import org.example.campuspark.geocoding.dto.CoordinateResponse;
import org.example.campuspark.geocoding.dto.KakaoApiResponse;
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

    // 주소명 -> 위도/경도 변환
    public CoordinateResponse getCoordinates(String address) {
        // WebClient를 사용하여 비동기 API 호출
        KakaoApiResponse kakaoResponse = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v2/local/search/address.json")
                .queryParam("query", address)
                .build())
            .header("Authorization", "KakaoAK " + kakaoApiKey)
            .retrieve() // 응답을 받아옴
            .bodyToMono(KakaoApiResponse.class)
            .block();

        if (kakaoResponse == null || kakaoResponse.getDocuments() == null
            || kakaoResponse.getDocuments().isEmpty()) {
            throw CustomException.from(ErrorCode.ERROR_IN_GEOCODING);
        }

        // 첫 번째 검색 결과에서 위도, 경도 추출
        KakaoApiResponse.Document firstDocument = kakaoResponse.getDocuments().get(0);
        double latitude = Double.parseDouble(firstDocument.getLatitude());
        double longitude = Double.parseDouble(firstDocument.getLongitude());

        return CoordinateResponse.of(latitude, longitude);
    }
}