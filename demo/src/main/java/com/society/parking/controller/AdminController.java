package com.society.parking.controller;

import com.society.parking.dto.BookingRequestDto;
import com.society.parking.model.Booking;
import com.society.parking.model.ParkingSlot;
import com.society.parking.model.User;
import com.society.parking.repository.BookingRepository;
import com.society.parking.repository.ParkingSlotRepository;
import com.society.parking.repository.UserRepository;
import com.society.parking.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import com.society.parking.service.UserService;
import org.springframework.stereotype.Controller;
import com.society.parking.service.BookingService;
import com.society.parking.service.ParkingSlotService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingSlotService parkingSlotService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;


    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("slots", parkingSlotService.getAllSlots());
        return "admin-dashboard";
    }

    @PostMapping("/slots/add")
    public String addParkingSlot(@RequestParam String slotNumber,
                                 @RequestParam String wing,
                                 RedirectAttributes redirectAttributes) {
        try {
            parkingSlotService.addParkingSlot(slotNumber,wing);
            redirectAttributes.addFlashAttribute("success", "Slot added successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/slots/{id}/deactivate")
    public String deactivateParkingSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            parkingSlotService.deactivateParkingSlot(id);
            redirectAttributes.addFlashAttribute("success", "Slot deactivated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/slots/{id}/activate")
    public String activateParkingSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            parkingSlotService.activateParkingSlot(id);
            redirectAttributes.addFlashAttribute("success", "Slot activated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }



    @GetMapping("/bookings")
    public String getAllBookings(@RequestParam(value = "filter", required = false) String filter,
                                 @RequestParam(value = "wing", required = false) String wing,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model) {

        List<Booking> bookings;

        // Filter by name and wing combined
        if (search != null && !search.isBlank()) {
            if (wing != null && !wing.isBlank()) {
                bookings = bookingRepository.findByUserNameAndWing(search, wing);
            } else {
                bookings = bookingRepository.findByUserName(search);
            }
        } else if (wing != null && !wing.isBlank()) {
            bookings = bookingRepository.findByWing(wing);
        } else if ("active".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByCancelledFalseAndEndTimeAfterOrderByStartTimeDesc(LocalDateTime.now());
        } else if ("cancelled".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findCancelledWithUserAndSlot();
        } else if ("completed".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByCancelledFalseAndEndTimeBeforeOrderByStartTimeDesc(LocalDateTime.now());
        } else if ("guest".equalsIgnoreCase(filter)) {
            bookings = bookingService.getGuestBookings();
        } else {
            bookings = bookingRepository.findAllWithUserAndSlot();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("filter", filter);
        model.addAttribute("selectedWing", wing);
        model.addAttribute("search", search);

        return "admin-bookings";
    }



    @PostMapping("/bookings/{id}/cancel")
    public String adminCancelBooking(@PathVariable Long id,
                                     @RequestParam(required = false) String reason,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id, "admin", true,reason);
            redirectAttributes.addFlashAttribute("success", "Booking #" + id + " cancelled by admin");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }


    @PostMapping("/bookings/{id}/restore")
    public String restoreBooking(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            bookingService.restoreBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking #" + id + " restored successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        model.addAttribute("counts", analyticsService.getBookingCounts());
        model.addAttribute("byWing", analyticsService.getWingDistribution());
        return "admin-analytics";
    }



    @GetMapping("/booking/new")
    public String showAdminBookingForm(@RequestParam(value = "userId", required = false) Long userId,
                                       Model model) {

        BookingRequestDto bookingDto = new BookingRequestDto();
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("booking", bookingDto);

        List<ParkingSlot> slots;

        if (userId != null) {
            User selectedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            bookingDto.setUserId(userId);
            slots = parkingSlotRepository.findByWingAndIsActiveTrue(selectedUser.getWing());
            model.addAttribute("selectedUser", selectedUser);
        } else {
            slots = parkingSlotRepository.findByIsActiveTrue();
        }

        // Attach booking info for each slot
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> slotBookingMap = new HashMap<>();
        for (ParkingSlot slot : slots) {
            List<Booking> bookings = bookingRepository
                    .findActiveBookingsForSlot(slot.getId(), now);
            slotBookingMap.put(slot.getId(), bookings);
        }

        model.addAttribute("slots", slots);
        model.addAttribute("slotBookingMap", slotBookingMap);

        return "admin-booking-form";
    }




    @GetMapping("/booking/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        return "admin-booking-details";
    }

    //admin override booking

    @PostMapping("/booking/new")
    public String processAdminBooking(@ModelAttribute BookingRequestDto booking,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (booking.isGuestBooking() && (booking.getUserId() == null || booking.getUserId() == 0)) {
                // Guest-only booking, no user selected
                bookingService.adminCreateBooking(booking);
            } else {
                // Booking on behalf of a registered user
                bookingService.adminCreateBooking(booking);
            }

            redirectAttributes.addFlashAttribute("success", "Booking created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @GetMapping("/users")
    public String showAllUsers(@RequestParam(value = "wing", required = false) String wing,
                               @RequestParam(value = "search", required = false) String search,
                               Model model) {

        List<User> users;

        if (search != null && !search.isBlank()) {
            if (wing != null && !wing.isBlank()) {
                users = userRepository.findByNameContainingIgnoreCaseAndWing(search, wing);
            } else {
                users = userRepository.findByNameContainingIgnoreCase(search);
            }
            model.addAttribute("search", search);
        } else {
            if (wing != null && !wing.isBlank()) {
                users = userRepository.findByWing(wing);
            } else {
                users = userRepository.findAll();
            }
        }

        model.addAttribute("users", users);
        model.addAttribute("selectedWing", wing == null ? "" : wing);
        return "admin-users";
    }






}
