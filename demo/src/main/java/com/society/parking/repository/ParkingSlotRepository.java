package com.society.parking.repository;

import com.society.parking.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByIsAvailableTrueAndIsActiveTrue();
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
    List<ParkingSlot> findAll();
    boolean existsBySlotNumberAndWing(String slotNumber, String wing);


    List<ParkingSlot> findByIsActiveTrueAndWing(String wing);

    List<ParkingSlot> findAllByIsActiveTrue();
    List<ParkingSlot> findByWingAndIsActiveTrue(String wing);
    List<ParkingSlot> findByIsActiveTrue();
}
