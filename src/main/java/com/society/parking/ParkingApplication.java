package com.society.parking;

import com.society.parking.model.User;
import com.society.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ParkingApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ParkingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (userRepository.findByEmail("admin@society.com") == null) {
			User admin = new User();
			admin.setEmail("admin@society.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setName("Admin");
			admin.setPhone("0000000000");
			admin.setFlatNumber("ADMIN");
			admin.setAdmin(true);
			admin.setWing("A");
			userRepository.save(admin);
		}
	}
}