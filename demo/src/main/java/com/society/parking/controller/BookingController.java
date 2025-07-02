package com.society.parking.controller;

import com.society.parking.exception.SlotNotAvailableException;
import com.society.parking.model.Booking;
import com.society.parking.model.ParkingSlot;
import com.society.parking.model.User;
import com.society.parking.repository.BookingRepository;
import com.society.parking.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.society.parking.dto.BookingRequestDto;
import com.society.parking.service.BookingService;
import com.society.parking.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ParkingSlotService parkingSlotService;

    @Autowired
    private BookingRepository bookingRepository;





    @GetMapping("/new")
    public String showBookingForm(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<ParkingSlot> availableSlots = parkingSlotService.getAvailableSlotsByWing(user.getWing());

        Map<Long, String> slotStatusMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (ParkingSlot slot : availableSlots) {
            List<Booking> futureBookings = bookingRepository
                    .findActiveBookingsForSlot(slot.getId(), now);

            if (!futureBookings.isEmpty()) {
                Booking next = futureBookings.get(0); // show first upcoming booking
                String time = next.getStartTime().toLocalDate()+" "+ next.getStartTime().toLocalTime() + "–" +next.getEndTime().toLocalDate()+ " "+next.getEndTime().toLocalTime();
                slotStatusMap.put(slot.getId(), "Booked: " + time);
            } else {
                slotStatusMap.put(slot.getId(), "Available");
            }
        }

        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("slotStatusMap", slotStatusMap);
        model.addAttribute("booking", new BookingRequestDto());

        return "new-booking";
    }





//    @PostMapping("/new")
//    public String createBooking(@ModelAttribute("booking") BookingRequestDto bookingRequest,
//                                Principal principal,
//                                RedirectAttributes redirectAttributes) {
//
//        try {
//            bookingService.createBooking(bookingRequest, principal.getName());
//            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
//            return "redirect:/user/dashboard";
//        }catch (SlotNotAvailableException e) {
//            // This will be handled by the GlobalExceptionHandler
//            throw e;
//        }
//    }


@PostMapping("/new")
    public String createBooking(@ModelAttribute("booking") BookingRequestDto bookingDto,
                                Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        bookingService.createBooking(bookingDto, user.getEmail());

        model.addAttribute("bookingSuccess", true);
        return "redirect:/user/dashboard";
    }






//    @GetMapping("/history")
//    public String getBookingHistory(Model model, Principal principal) {
//        String email = principal.getName();
//        List<Booking> userBookings = bookingService.getUserBookings(email);
//        model.addAttribute("bookings", userBookings);
//        return "booking-history"; // Thymeleaf template
//    }

    @GetMapping("/history")
    public String getBookingHistory(@RequestParam(value = "filter", defaultValue = "all") String filter,
                                    Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        // Get filtered bookings
        List<Booking> bookings;
        switch (filter) {
            case "active" -> bookings = bookingRepository.findActiveBookings(user.getId());
            case "completed" -> bookings = bookingRepository.findCompletedBookings(user.getId());
            case "cancelled" -> bookings = bookingRepository.findCancelledBookings(user.getId());
            default -> bookings = bookingRepository.findAllByUserId(user.getId());
        }

        // Add bookings and filter info to model
        model.addAttribute("bookings", bookings);
        model.addAttribute("activeTab", filter);

        // Add status counts
        model.addAttribute("activeCount", bookingRepository.countActiveByUser(user.getId()));
        model.addAttribute("completedCount", bookingRepository.countCompletedByUser(user.getId()));
        model.addAttribute("cancelledCount", bookingRepository.countCancelledByUser(user.getId()));
        model.addAttribute("totalCount", bookingRepository.countByUserId(user.getId()));

        return "booking-history";
    }




    //cancle booking
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {

            bookingService.cancelBooking(id, principal.getName(),false,null);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/booking/history"; // Redirect to bookings hostory
    }
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }


    @GetMapping("/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model, Principal principal) {
        Booking booking = bookingService.findByIdAndUserEmail(id, principal.getName());
        model.addAttribute("booking", booking);
        return "booking-details"; // You need to create this page
    }

    // Other booking-related endpoints...
}
