package org.example.campuspark.parkingspace.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.campuspark.global.domain.BaseEntity;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.user.domain.User;

@Entity
@Table(name = "parkingspaces")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ParkingSpace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String address;

    private String name;

    private Double latitude;

    private Double longitude;

    private LocalTime availableStartTime;

    private LocalTime availableEndTime;

    private Integer price;

    private Boolean status;

    private int availableCount;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ParkingSpacePhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    public ParkingSpace(
            String address,
            String name,
            Double latitude,
            Double longitude,
            LocalTime availableStartTime,
            LocalTime availableEndTime,
            Integer price,
            Integer availableCount,
            String thumbnailUrl

    ) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.price = price;
        this.availableCount = availableCount;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateStatus(Boolean status) {
        this.status = status;
    }

}
