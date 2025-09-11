package org.example.campuspark.parkingspace.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.campuspark.global.domain.BaseEntity;
import org.example.campuspark.reservation.domain.Reservation;
import org.example.campuspark.user.domain.UserEntity;

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
    private UserEntity user;

    private String address;

    private Double latitude;

    private Double longitude;

    private String availableHours;

    private Integer pricePerHour;

    private Boolean status;

    // 주차 가능 대수
    private int availableArea;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
}
