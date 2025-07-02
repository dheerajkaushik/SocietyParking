package com.society.parking.service;

import com.society.parking.dto.UserRegistrationDto;
import com.society.parking.model.User;
import com.society.parking.model.VerificationToken;
import com.society.parking.repository.UserRepository;
import com.society.parking.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());
        user.setPhone(registrationDto.getPhone());
        user.setFlatNumber(registrationDto.getFlatNumber());
        user.setWing(registrationDto.getWing());
        user.setAdmin(false);

        user= userRepository.save(user);


        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String link = "http://societyparking-production.up.railway.app/verify?token=" + token;
        emailService.sendSimpleMessage(user.getEmail(), "Email Verification",
                "Click this link to verify: " + link);

        return user;

    }



    // Other methods...
}
