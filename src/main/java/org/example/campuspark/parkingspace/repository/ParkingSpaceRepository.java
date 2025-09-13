package org.example.campuspark.parkingspace.repository;

import java.util.Optional;
import org.example.campuspark.parkingspace.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    @Query("SELECT p FROM ParkingSpace p WHERE p.status = true AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * " +
           "cos(radians(p.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.latitude)))) <= :radiusKm")
    List<ParkingSpace> findNearbyParkingSpaces(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm);

    Optional<ParkingSpace> findParkingSpaceById(Long parkingSpaceId);
}

