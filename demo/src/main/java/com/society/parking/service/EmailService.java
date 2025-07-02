package com.society.parking.service;

import com.society.parking.model.Booking;
import com.society.parking.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendBookingConfirmation(User user, Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Parking Booking Confirmation");

        String content = String.format(
                "Dear %s,\n\nYour parking slot booking is confirmed.\n\n" +
                        "Slot Number: %s\n" +
                        "Date: %s\n" +
                        "Time: %s to %s\n\n" +
                        "Thank you for using our parking system.",
                user.getName(),
                booking.getParkingSlot().getSlotNumber(),
                booking.getStartTime().toLocalDate(),
                booking.getStartTime().toLocalTime(),
                booking.getEndTime().toLocalTime()
        );

        message.setText(content);
        mailSender.send(message);
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@society.com"); // or get from application.properties
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }



    public void sendGuestBookingConfirmation(Booking booking) {
        if (booking.getGuestEmail() == null || booking.getGuestEmail().isEmpty()) return;

        String bookedBy = "Society Admin";
        if (booking.getUser() != null) {
            bookedBy = String.format("%s (%s)", booking.getUser().getName(), booking.getUser().getEmail());
        }

        String subject = "Guest Parking Slot Confirmation";
        String message = String.format(
                "Dear %s,\n\nA parking slot has been booked for your visit.\n\n" +
                        "Slot Number: %s\nStart: %s\nEnd: %s\n\n" +
                        "Booked By: %s\n\nThank you!",
                booking.getGuestName(),
                booking.getParkingSlot().getSlotNumber(),
                booking.getStartTime(),
                booking.getEndTime(),
                bookedBy
        );

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(booking.getGuestEmail());
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send guest booking email: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // Other email methods...
}
