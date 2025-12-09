package com.society.parking.service;

import com.society.parking.model.ParkingSlot;
import com.society.parking.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotService {
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    public ParkingSlot addParkingSlot(String slotNumber,String wing) {
        // Check if slot already exists
        if (parkingSlotRepository.existsBySlotNumberAndWing(slotNumber,wing)) {
            throw new RuntimeException("Slot number already exists");
        }

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(slotNumber);
        slot.setWing(wing);
        slot.setActive(true);
        slot.setAvailable(true);

        return parkingSlotRepository.save(slot);
    }

    public void deactivateParkingSlot(Long slotId) {
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + slotId));

        if (!slot.isActive()) {
            throw new RuntimeException("Slot is already deactivated");
        }

        slot.setActive(false);
        slot.setAvailable(false); // Also mark as unavailable
        parkingSlotRepository.save(slot);
    }

    public void activateParkingSlot(Long slotId) {
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + slotId));

        if (slot.isActive()) {
            throw new RuntimeException("Slot is already active");
        }

        slot.setActive(true);
        slot.setAvailable(true);
        parkingSlotRepository.save(slot);
    }

    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByIsAvailableTrueAndIsActiveTrue();
    }

    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }

    public List<ParkingSlot> getAvailableSlotsByWing(String wing) {
        return parkingSlotRepository.findByIsActiveTrueAndWing(wing);
    }

}