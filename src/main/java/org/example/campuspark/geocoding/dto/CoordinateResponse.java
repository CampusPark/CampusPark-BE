package org.example.campuspark.geocoding.dto;

public record CoordinateResponse(
    Double latitude,
    Double longitude
) {

    public static CoordinateResponse of(Double latitude, Double longitude) {
        return new CoordinateResponse(latitude, longitude);
    }
}
