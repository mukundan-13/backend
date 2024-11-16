package com.rentwheelz.rentwheelz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rentwheelz.rentwheelz.model.Review;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    Optional<Review> findByUserIdAndBookingId(Long userId, Long bookingId);
    List<Review> findByBookingId(Long bookingId);
    List<Review> findByVehicleId(Long vehicleId);
    List<Review> findByUserId(Long userId);
}