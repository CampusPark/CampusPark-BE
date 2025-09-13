package org.example.campuspark.parkingspace.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_space_photos")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ParkingSpacePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace parkingSpace;

    @Column(nullable = false)
    private String imageUrl;

    public static ParkingSpacePhoto create(ParkingSpace parkingSpace, String imageUrl) {
        return ParkingSpacePhoto.builder()
                .parkingSpace(parkingSpace)
                .imageUrl(imageUrl)
                .build();
    }
}
