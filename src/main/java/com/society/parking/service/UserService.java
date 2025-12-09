package com.society.parking.service;

import com.society.parking.dto.UserRegistrationDto;
import com.society.parking.model.User;
import com.society.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        return userRepository.save(user);
    }

    // Other methods...
}
