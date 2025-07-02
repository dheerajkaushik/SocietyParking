package com.society.parking.service;

import com.society.parking.model.Booking;
import com.society.parking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private BookingRepository bookingRepository;

    public Map<String, Long> getBookingCounts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate todayStart = now.toLocalDate();
        LocalDateTime todayEnd = todayStart.atTime(LocalTime.MAX);

        LocalDateTime weekStart = todayStart.minusDays(6).atStartOfDay();
        LocalDateTime monthStart = todayStart.withDayOfMonth(1).atStartOfDay();

        long todayCount = bookingRepository
                .findByStartTimeBetweenAndCancelledFalse(todayStart.atStartOfDay(), todayEnd)
                .size();

        long weekCount = bookingRepository
                .findByStartTimeBetweenAndCancelledFalse(weekStart, todayEnd)
                .size();

        long monthCount = bookingRepository
                .findByStartTimeBetweenAndCancelledFalse(monthStart, todayEnd)
                .size();

        Map<String, Long> result = new LinkedHashMap<>();
        result.put("today", todayCount);
        result.put("last7Days", weekCount);
        result.put("thisMonth", monthCount);
        return result;
    }

    public Map<String, Long> getWingDistribution() {
        List<Booking> allBookings = bookingRepository.findByCancelledFalse();
        return allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getParkingSlot().getWing(),
                        Collectors.counting()
                ));
    }
}

