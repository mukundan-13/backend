package com.rentwheelz.rentwheelz.service;

import com.rentwheelz.rentwheelz.model.Review;
import com.rentwheelz.rentwheelz.model.User;
import com.rentwheelz.rentwheelz.model.Vehicle;
import com.rentwheelz.rentwheelz.model.Booking;
import com.rentwheelz.rentwheelz.repository.ReviewRepository;
import com.rentwheelz.rentwheelz.repository.UserRepository;
import com.rentwheelz.rentwheelz.repository.VehicleRepository;
import com.rentwheelz.rentwheelz.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Review addReview(Review review) {
        Long userId = review.getUser().getId();
        Long vehicleId = review.getVehicle().getId();
        Long bookingId = review.getBooking().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));

        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookingId(userId, bookingId);
        Review savedReview;

        if (existingReview.isPresent()) {
            // Update existing review
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.setcomment(review.getcomment());
            reviewToUpdate.setrating(review.getrating());
            reviewToUpdate.setModifiedAt(LocalDateTime.now());
            savedReview = reviewRepository.save(reviewToUpdate);
        } else {
            // Create new review
            review.setUser(user);
            review.setVehicle(vehicle);
            review.setBooking(booking);
            review.setCreatedAt(LocalDateTime.now());
            review.setModifiedAt(LocalDateTime.now());
            savedReview = reviewRepository.save(review);
        }

        // Calculate the average rating for the vehicle
        updateVehicleAverageRating(vehicleId);

        return savedReview;
    }

    private void updateVehicleAverageRating(Long vehicleId) {
        List<Review> reviews = reviewRepository.findByVehicleId(vehicleId);

        if (!reviews.isEmpty()) {
            // Calculate the average rating
            double averageRating = reviews.stream()
                    .mapToDouble(review -> Double.parseDouble(review.getrating()))
                    .average()
                    .orElse(0.0);

            // Update the rating in the Vehicle entity
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID"));
            vehicle.setrating(String.format("%.1f", averageRating)); // Convert to String with 1 decimal place
            vehicleRepository.save(vehicle);
        }
    }

    public Optional<Review> getReviewByUserAndBooking(Long userId, Long bookingId) {
        return reviewRepository.findByUserIdAndBookingId(userId, bookingId);
    }

    public List<Review> getReviewsByBookingId(Long bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }

    public List<Review> getReviewsByVehicleId(Long vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId);
    }

    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
}