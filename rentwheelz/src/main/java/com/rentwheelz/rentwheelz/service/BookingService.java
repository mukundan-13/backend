package com.rentwheelz.rentwheelz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rentwheelz.rentwheelz.model.Booking;
import com.rentwheelz.rentwheelz.model.Vehicle;
import com.rentwheelz.rentwheelz.model.User;
import com.rentwheelz.rentwheelz.repository.BookingRepository;
import com.rentwheelz.rentwheelz.repository.UserRepository;
import com.rentwheelz.rentwheelz.repository.VehicleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender; // For sending OTP emails

    public Booking createBooking(Long vehicleId, Long customerId, LocalDate startDate, LocalDate endDate,
            String totalPrice, String status) {
        Booking booking = new Booking();
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(status);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setModifiedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    // Updated method to accept Booking object directly and check for conflicts
    public Booking createBooking(Booking booking) {
        // Fetch the Vehicle and User entities by their IDs
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Set the fetched entities on the booking
        booking.setVehicle(vehicle);
        booking.setUser(user);

        // Check for existing conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                booking.getVehicle().getId(),
                booking.getStartDate(),
                booking.getEndDate());

        if (!conflictingBookings.isEmpty()) {
            throw new IllegalArgumentException("The vehicle is already booked for the specified time period.");
        }

        // No conflicts found, proceed with creating the booking
        booking.setStatus("Booked"); // Set default status
        booking.setCreatedAt(LocalDateTime.now());
        booking.setModifiedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        try {
            sendBookingConfirmationEmail(savedBooking);
        } catch (Exception e) {
            System.err.println("Error sending booking confirmation email: " + e.getMessage());
        }

        return savedBooking;
    }

    private void sendBookingConfirmationEmail(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Confirmation - RentWheelZ");

        // Customize the message content with booking details
        String emailContent = "Dear " + booking.getUser().getFirstName() + " " + booking.getUser().getLastName()
                + ",\n\n"
                + "Thank you for booking with RentWheelZ! Your booking has been successfully confirmed.\n\n"
                + "Booking Details:\n"
                + "Car: " + booking.getVehicle().getcompany_name() + " " + booking.getVehicle().getmodel() + "\n"
                + "Booking ID: " + booking.getId() + "\n"
                + "Pickup Date: " + booking.getStartDate() + "\n"
                + "Return Date: " + booking.getEndDate() + "\n"
                + "Total Price: " + booking.getTotalPrice() + "\n\n"
                + "We hope you enjoy your ride! If you have any questions or need assistance, feel free to contact us.\n\n"
                + "Best regards,\nThe RentWheelZ Team";

        message.setText(emailContent);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending booking confirmation email: " + e.getMessage());
        }
    }

    public Booking cancelBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setStatus("Canceled");
            booking.setModifiedAt(LocalDateTime.now());

            // Save the updated booking status
            Booking updatedBooking = bookingRepository.save(booking);

            try {
                sendBookingCancellationEmail(updatedBooking); // Send cancellation email
            } catch (Exception e) {
                System.err.println("Error sending booking cancellation email: " + e.getMessage());
            }

            return updatedBooking;
        } else {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
    }

    private void sendBookingCancellationEmail(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Cancellation - RentWheelZ");

        // Customize the message content for booking cancellation
        String emailContent = "Dear " + booking.getUser().getFirstName() + " " + booking.getUser().getLastName()
                + ",\n\n"
                + "We regret to inform you that your booking with RentWheelZ has been canceled.\n\n"
                + "Booking Details:\n"
                + "Car: " + booking.getVehicle().getcompany_name() + " " + booking.getVehicle().getmodel() + "\n"
                + "Booking ID: " + booking.getId() + "\n"
                + "Pickup Date: " + booking.getStartDate() + "\n"
                + "Return Date: " + booking.getEndDate() + "\n"
                + "Total Price: " + booking.getTotalPrice() + "\n\n"
                + "We apologize for any inconvenience caused. If you have any further questions or require assistance, please do not hesitate to reach out to us.\n\n"
                + "Best regards,\nThe RentWheelZ Team";

        message.setText(emailContent);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending booking cancellation email: " + e.getMessage());
        }
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByUserIdAndStatus(customerId, "Booked");
    }


    public List<Booking> getBookedVehicleDetails(Long vehicleId) {
        return bookingRepository.findByVehicleIdAndStatus(vehicleId, "Booked");
    }
    

    // Method to check and complete expired bookings
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void completeExpiredBookings() {
        LocalDate now = LocalDate.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndEndDateBefore("Booked", now);
        for (Booking booking : expiredBookings) {
            booking.setStatus("Completed");
            booking.setModifiedAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }
    }

}