package com.society.parking.service;
import com.society.parking.dto.BookingRequestDto;
import com.society.parking.exception.BookingNotFoundException;
import com.society.parking.exception.SlotNotAvailableException;
import com.society.parking.exception.UnauthorizedAccessException;
import com.society.parking.model.Booking;
import com.society.parking.model.ParkingSlot;
import com.society.parking.model.User;
import com.society.parking.repository.BookingRepository;
import com.society.parking.repository.ParkingSlotRepository;
import com.society.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

//    public Booking createBooking(BookingRequestDto bookingRequest, String userEmail) {
//        User user = userRepository.findByEmail(userEmail);
//        if (user == null) throw new RuntimeException("User not found");
//
//        ParkingSlot slot = parkingSlotRepository.findById(bookingRequest.getSlotId())
//                .orElseThrow(() -> new RuntimeException("Slot not found"));
//
//        if (!user.isAdmin() && !slot.getWing().equalsIgnoreCase(user.getWing())) {
//            throw new UnauthorizedAccessException("You can only book slots in your own wing.");
//        }
//
//        // Check slot availability
//        if (!isSlotAvailable(slot, bookingRequest.getStartTime(), bookingRequest.getEndTime())) {
//            throw new SlotNotAvailableException("Slot not available for the selected time");
//        }
//
//        Booking booking = new Booking();
//        booking.setUser(user);
//        booking.setParkingSlot(slot);
//        booking.setBookingDate(LocalDateTime.now());
//        booking.setStartTime(bookingRequest.getStartTime());
//        booking.setEndTime(bookingRequest.getEndTime());
//        booking.setGuestBooking(bookingRequest.isGuestBooking());
//
//        booking.setCancelled(false);
//
//        if (bookingRequest.isGuestBooking()) {
//            booking.setGuestName(bookingRequest.getGuestName());
//            booking.setGuestVehicleNumber(bookingRequest.getGuestVehicleNumber());
//            booking.setGuestEmail(bookingRequest.getGuestEmail());
//
//            emailService.sendGuestBookingConfirmation(booking);
//        }
//
//        Booking savedBooking = bookingRepository.save(booking);
//
//        // Send confirmation email
//        emailService.sendBookingConfirmation(user, savedBooking);
//
//        return savedBooking;
//    }

    public Booking createBooking(BookingRequestDto bookingRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException("User not found");

        ParkingSlot slot = parkingSlotRepository.findById(bookingRequest.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!user.isAdmin() && !slot.getWing().equalsIgnoreCase(user.getWing())) {
            throw new UnauthorizedAccessException("You can only book slots in your own wing.");
        }

        LocalDateTime currentStart = bookingRequest.getStartTime();
        LocalDateTime currentEnd = bookingRequest.getEndTime();

        Booking lastBooking = null;

        if (bookingRequest.isRecurring()) {
            LocalDate endRecurrence = bookingRequest.getRecurrenceEndDate();
            if (endRecurrence == null || bookingRequest.getRecurrencePattern() == null) {
                throw new IllegalArgumentException("Recurrence pattern and end date are required for recurring bookings.");
            }

            while (!currentStart.toLocalDate().isAfter(endRecurrence)) {
                if (!isSlotAvailable(slot, currentStart, currentEnd)) {
                    currentStart = increment(currentStart, bookingRequest.getRecurrencePattern());
                    currentEnd = increment(currentEnd, bookingRequest.getRecurrencePattern());
                    continue;
                }

                Booking b = buildBookingEntity(bookingRequest, slot, user, currentStart, currentEnd);
                lastBooking = bookingRepository.save(b);

                if (b.isGuestBooking()) emailService.sendGuestBookingConfirmation(b);
                else emailService.sendBookingConfirmation(user, b);

                currentStart = increment(currentStart, bookingRequest.getRecurrencePattern());
                currentEnd = increment(currentEnd, bookingRequest.getRecurrencePattern());
            }

        } else {
            if (!isSlotAvailable(slot, currentStart, currentEnd)) {
                throw new SlotNotAvailableException("Slot not available for the selected time");
            }

            Booking b = buildBookingEntity(bookingRequest, slot, user, currentStart, currentEnd);
            lastBooking = bookingRepository.save(b);

            if (b.isGuestBooking()) emailService.sendGuestBookingConfirmation(b);
            else emailService.sendBookingConfirmation(user, b);
        }

        return lastBooking;
    }

    //additional helper method
    private Booking buildBookingEntity(BookingRequestDto dto, ParkingSlot slot, User user,
                                       LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setParkingSlot(slot);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setCancelled(false);
        booking.setGuestBooking(dto.isGuestBooking());
        //recurring--
        booking.setRecurring(dto.isRecurring());
        booking.setRecurrencePattern(dto.getRecurrencePattern());
        booking.setRecurrenceEndDate(dto.getRecurrenceEndDate());

        if (dto.isGuestBooking()) {
            booking.setGuestName(dto.getGuestName());
            booking.setGuestVehicleNumber(dto.getGuestVehicleNumber());
            booking.setGuestEmail(dto.getGuestEmail());
        }

        return booking;
    }
//increment method of recurring
private LocalDateTime increment(LocalDateTime dateTime, String pattern) {
    switch (pattern.toUpperCase()) {
        case "DAILY":
            return dateTime.plusDays(1);
        case "WEEKLY":
            return dateTime.plusWeeks(1);
        default:
            throw new IllegalArgumentException("Unsupported recurrence pattern: " + pattern);
    }
}




    private boolean isSlotAvailable(ParkingSlot slot, LocalDateTime start, LocalDateTime end) {
        List<Booking> conflictingBookings = bookingRepository
                .findByParkingSlotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndCancelledFalse(
                        slot, end, start);
        return conflictingBookings.isEmpty();
    }



    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(String email) {
        return bookingRepository.findByUserEmailOrderByStartTimeDesc(email);
    }



    @Transactional
    public void cancelBooking(Long bookingId, String userEmail,boolean isAdmin,String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        // Verify the booking belongs to the user
        if (!isAdmin && !booking.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("You can only cancel your own bookings");
        }

        // Check if already cancelled
        if (booking.isCancelled()) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Check if cancellation is allowed (e.g., not too close to start time)
        if (!isAdmin && LocalDateTime.now().isAfter(booking.getStartTime().minusHours(2))) {
            throw new IllegalStateException("Cannot cancel within 2 hours of booking start time");
        }

        booking.setCancelled(true);
        booking.setCancellationTime(LocalDateTime.now());
        booking.setCancelledBy(isAdmin ? "ADMIN" : "USER");
        booking.setCancellationReason(reason);
        bookingRepository.save(booking);

        if (isAdmin) {
            String subject = "Booking #" + booking.getId() + " Cancelled by Admin";
            String message = String.format(
                    "Dear %s,\n\nYour booking for slot %s on %s at %s has been cancelled by the admin.\\n\\nReason: %s \n\nRegards,\nSociety Parking",
                    booking.getUser().getName(),
                    booking.getParkingSlot().getSlotNumber(),
                    booking.getStartTime().toLocalDate(),
                    booking.getStartTime().toLocalTime(),
            (reason != null && !reason.isEmpty()) ? reason : "Not specified"
            );

            emailService.sendSimpleMessage(booking.getUser().getEmail(), subject, message);
        }
    }

    @Transactional
    public void restoreBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.isCancelled()) {
            throw new IllegalStateException("Booking is not cancelled");
        }

        if (!isSlotAvailable(booking.getParkingSlot(), booking.getStartTime(), booking.getEndTime())) {
            throw new SlotNotAvailableException("Original slot is no longer available");
        }

        booking.setCancelled(false);
        booking.setCancellationTime(null);
        booking.setCancelledBy(null);
        booking.setCancellationReason(null);
        bookingRepository.save(booking);
    }


    public Booking findByIdAndUserEmail(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to booking");
        }
        return booking;
    }


    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
    }

    public List<Booking> getGuestBookings() {
        return bookingRepository.findByGuestBookingTrue();
    }


    //admin booking for user
    public Booking adminCreateBooking(BookingRequestDto dto) {
        ParkingSlot slot = parkingSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!isSlotAvailable(slot, dto.getStartTime(), dto.getEndTime())) {
            throw new SlotNotAvailableException("Slot is not available in selected time");
        }

        Booking booking = new Booking();
        booking.setParkingSlot(slot);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setGuestBooking(dto.isGuestBooking());
        booking.setCancelled(false);

        // If booking for guest
        if (dto.isGuestBooking() && (dto.getUserId() == null || dto.getUserId() == 0)) {

            if (dto.getGuestEmail() == null || dto.getGuestEmail().isBlank()) {
                throw new IllegalArgumentException("Guest email is required for guest bookings.");
            }

            booking.setGuestName(dto.getGuestName());
            booking.setGuestVehicleNumber(dto.getGuestVehicleNumber());
            booking.setGuestEmail(dto.getGuestEmail());
        }
        else {
            // If booking for registered user
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            booking.setUser(user);
        }

        Booking saved = bookingRepository.save(booking);

        // Send mail
        if (booking.getUser() != null) {
            emailService.sendBookingConfirmation(booking.getUser(), saved);
        }
        if (booking.isGuestBooking()) {
            emailService.sendGuestBookingConfirmation(saved);
        }

        return saved;
    }





    // Other methods...
}
