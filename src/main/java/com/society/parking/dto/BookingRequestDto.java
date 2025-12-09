package com.society.parking.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingRequestDto {

    //@NotNull(message="userId required")
    private Long userId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    private boolean isGuestBooking;
    private String guestName;
    private String guestVehicleNumber;

    @Email
    @NotBlank(message = "Guest email is required for guest bookings")
    private String guestEmail;


    //-for recurring
    private boolean recurring;
    private String recurrencePattern; // "DAILY", "WEEKLY"
    private LocalDate recurrenceEndDate;



    // Getters and Setters


    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }
//--recurring
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public boolean isGuestBooking() { return isGuestBooking; }
    public void setGuestBooking(boolean guestBooking) { isGuestBooking = guestBooking; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestVehicleNumber() { return guestVehicleNumber; }
    public void setGuestVehicleNumber(String guestVehicleNumber) { this.guestVehicleNumber = guestVehicleNumber; }
}