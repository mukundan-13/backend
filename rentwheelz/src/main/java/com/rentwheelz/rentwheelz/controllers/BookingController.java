package com.rentwheelz.rentwheelz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rentwheelz.rentwheelz.model.Booking;
import com.rentwheelz.rentwheelz.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating booking");
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            Booking createdBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error canceling booking.");
        }
    }


    // Existing endpoint to fetch bookings of a user
    @GetMapping("/user/{customerId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long customerId) {
        List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(bookings); // Includes vehicle details now
    }

    @GetMapping("/vehicle/{vehicleId}/booked-details")
public ResponseEntity<List<Booking>> getBookedVehicleDetails(@PathVariable Long vehicleId) {
    List<Booking> bookings = bookingService.getBookedVehicleDetails(vehicleId);
    return ResponseEntity.ok(bookings);
}

}