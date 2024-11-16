package com.rentwheelz.rentwheelz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rentwheelz.rentwheelz.model.Review;
import com.rentwheelz.rentwheelz.service.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            Review savedReview = reviewService.addReview(review);
            return ResponseEntity.ok(savedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding review.");
        }
    }

    @GetMapping("/booking/{bookingId}/user/{userId}")
    public ResponseEntity<Review> getReviewByUserAndBooking(@PathVariable Long userId, @PathVariable Long bookingId) {
        Optional<Review> review = reviewService.getReviewByUserAndBooking(userId, bookingId);
        return review.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Review>> getReviewsByBooking(@PathVariable Long bookingId) {
        List<Review> reviews = reviewService.getReviewsByBookingId(bookingId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Review>> getReviewsByVehicle(@PathVariable Long vehicleId) {
        List<Review> reviews = reviewService.getReviewsByVehicleId(vehicleId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
}