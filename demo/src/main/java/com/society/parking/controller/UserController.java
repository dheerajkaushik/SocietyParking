package com.society.parking.controller;

import com.society.parking.model.Booking;
import com.society.parking.model.User;
import com.society.parking.model.VerificationToken;
import com.society.parking.repository.BookingRepository;
import com.society.parking.repository.UserRepository;
import com.society.parking.repository.VerificationTokenRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.society.parking.dto.UserRegistrationDto;
import com.society.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register"; // This must be the name of your Thymeleaf form page
        }
        userService.registerUser(registrationDto);
        return "redirect:/login?registered";
    }




//    @GetMapping("/dashboard")
//    public String userDashboard() {
//        return "user-dashboard"; // Ensure this template exists
//    }

    @GetMapping("/dashboard")
    public String userDashboard(Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("activeBookingsCount", bookingRepository.countActiveByUser(user.getId()));
        model.addAttribute("completedBookingsCount", bookingRepository.countCompletedByUser(user.getId()));
        model.addAttribute("cancelledBookingsCount", bookingRepository.countCancelledByUser(user.getId()));
        model.addAttribute("nextBooking", bookingRepository.findTopByUserIdAndCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(user.getId(), LocalDateTime.now()).orElse(null));


        List<Booking> guestBookings = bookingRepository.findGuestBookingsByUser(user.getId());
        model.addAttribute("guestBookings", guestBookings);



        return "user-dashboard"; // or whatever the view name is
    }





    // Other user-related endpoints...
}
