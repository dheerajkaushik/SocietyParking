package com.society.parking.repository;

import com.society.parking.model.Booking;
import com.society.parking.model.ParkingSlot;
import com.society.parking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Booking> findByParkingSlotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndCancelledFalse(
            ParkingSlot parkingSlot, LocalDateTime end, LocalDateTime start);
    List<Booking> findByUserEmailOrderByStartTimeDesc(String email);
    List<Booking> findAllByOrderByStartTimeDesc();

    List<Booking> findByCancelledFalseOrderByStartTimeDesc();

    List<Booking> findByCancelledTrueOrderByStartTimeDesc();

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user u JOIN FETCH b.parkingSlot s ORDER BY b.startTime DESC")
    List<Booking> findAllWithUserAndSlot();


    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.parkingSlot WHERE b.cancelled = false ORDER BY b.startTime DESC")
    List<Booking> findActiveWithUserAndSlot();

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user u JOIN FETCH b.parkingSlot s WHERE b.cancelled = true ORDER BY b.startTime DESC")
    List<Booking> findCancelledWithUserAndSlot();


    List<Booking> findByCancelledFalseAndEndTimeAfterOrderByStartTimeDesc(LocalDateTime now);
    List<Booking> findByCancelledFalseAndEndTimeBeforeOrderByStartTimeDesc(LocalDateTime now);

    List<Booking> findByCancelledFalse();
    List<Booking> findByStartTimeBetweenAndCancelledFalse(LocalDateTime start, LocalDateTime end);
    List<Booking> findByGuestBookingTrue();

    @Query("SELECT b FROM Booking b WHERE b.parkingSlot.id = :slotId AND b.cancelled = false AND b.endTime > :now ORDER BY b.startTime ASC")
    List<Booking> findActiveBookingsForSlot(@Param("slotId") Long slotId, @Param("now") LocalDateTime now);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime > CURRENT_TIMESTAMP")
    int countActiveByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime <= CURRENT_TIMESTAMP")
    int countCompletedByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = true")
    int countCancelledByUser(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.startTime > CURRENT_TIMESTAMP ORDER BY b.startTime ASC LIMIT 1")
    Booking findNextBookingByUser(@Param("userId") Long userId);

    Optional<Booking> findTopByUserIdAndCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.guestBooking = true ORDER BY b.startTime DESC")
    List<Booking> findGuestBookingsByUser(@Param("userId") Long userId);


    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime > CURRENT_TIMESTAMP")
    List<Booking> findActiveBookings(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime <= CURRENT_TIMESTAMP")
    List<Booking> findCompletedBookings(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.cancelled = true")
    List<Booking> findCancelledBookings(@Param("userId") Long userId);

    List<Booking> findAllByUserId(Long userId);

//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime > CURRENT_TIMESTAMP")
//    int countActiveByUser(@Param("userId") Long userId);
//
//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = false AND b.endTime <= CURRENT_TIMESTAMP")
//    int countCompletedByUser(@Param("userId") Long userId);
//
//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.cancelled = true")
//    int countCancelledByUser(@Param("userId") Long userId);

    int countByUserId(Long userId);
    @Query("SELECT b FROM Booking b WHERE LOWER(b.user.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Booking> findByUserName(@Param("name") String name);

    @Query("SELECT b FROM Booking b WHERE b.parkingSlot.wing = :wing")
    List<Booking> findByWing(@Param("wing") String wing);

    @Query("SELECT b FROM Booking b WHERE LOWER(b.user.name) LIKE LOWER(CONCAT('%', :name, '%')) AND b.parkingSlot.wing = :wing")
    List<Booking> findByUserNameAndWing(@Param("name") String name, @Param("wing") String wing);






}
