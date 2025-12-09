package com.society.parking.repository;

import com.society.parking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByWing(String wing);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByNameContainingIgnoreCaseAndWing(String name, String wing);

}



