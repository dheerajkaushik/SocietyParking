package com.society.parking.controller;

import com.society.parking.model.User;
import com.society.parking.model.VerificationToken;
import com.society.parking.repository.UserRepository;
import com.society.parking.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class VerificationController {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired.");
        }

        User user = verificationToken.getUser();

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        System.out.println("Before enabling: " + user.getEmail() + " -> enabled = " + user.isEnabled());

        user.setEnabled(true); // ✅ this must update the entity
        userRepository.save(user); // ✅ persist change to DB


        System.out.println("After saving: " + user.getEmail() + " -> enabled = " + user.isEnabled());

        return ResponseEntity.ok("Email verified successfully. You can now log in.");
    }

}
