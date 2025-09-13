package org.example.campuspark.geocoding.dto;

import java.util.List;

public record KakaoKeywordResponse(
        Meta meta,
        List<Document> documents
) {
    public record Meta(
            int total_count,
            int pageable_count,
            boolean is_end,
            SameName same_name
    ) {}

    public record SameName(
            List<String> region,
            String keyword,
            String selected_region
    ) {}

    public record Document(
            String id,
            String place_name,
            String category_name,
            String category_group_code,
            String category_group_name,
            String phone,
            String address_name,
            String road_address_name,
            String x, // 경도
            String y, // 위도
            String place_url,
            String distance
    ) {}
}