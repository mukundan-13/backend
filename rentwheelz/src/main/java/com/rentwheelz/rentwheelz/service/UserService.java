package com.rentwheelz.rentwheelz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rentwheelz.rentwheelz.model.User;
import com.rentwheelz.rentwheelz.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender; // For sending OTP emails

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User user) throws Exception {
        // Check if email is already registered
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email Already Register.");
        }

        // Check if passwords match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new Exception("Password Not Matched.");
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set creation and modification timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        // Save the user to the repository
        User savedUser = userRepository.save(user);

        // Send registration success email
        sendRegistrationSuccessEmail(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName());

        return savedUser; // Return the saved user object
    }

    private void sendRegistrationSuccessEmail(String email, String firstName, String lastName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registration Successful - RentWheelZ");
        message.setText("Dear " + firstName + " " + lastName
                + ",\n\nYou have been successfully registered on RentWheelZ.\n\nNow you can start booking cars and enjoy your journey!\n\nBest regards,\nThe RentWheelZ Team");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            System.err.println("Error sending registration success email: " + e.getMessage());
        }
    }

    // Login API
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    // Update is_logged_in to true upon successful login
                    user.setIsLoggedIn(true);
                    userRepository.save(user); // Save the updated user
                    return user;
                });
    }

    // Logout API
    public Optional<User> logout(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User existingUser = user.get();
            // Set is_logged_in to false upon logout
            existingUser.setIsLoggedIn(false);
            userRepository.save(existingUser); // Save the updated user
            return Optional.of(existingUser);
        }
        return Optional.empty();
    }

    // Method to send OTP for password reset
    public Optional<User> initiatePasswordReset(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User existingUser = user.get();
            String otp = generateOtp();
            existingUser.setOtp(otp);
            existingUser.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // Set OTP expiry
            userRepository.save(existingUser);
            sendOtpEmail(email, otp); // Send the OTP via email
            return Optional.of(existingUser); // Return the user object after saving
        }
        return Optional.empty(); // Return an empty Optional if user is not found
    }

    // Method to verify OTP and reset password
    public Optional<User> resetPasswordWithOtp(String email, String otp, String newPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User existingUser = user.get();
            // Check OTP validity
            if (existingUser.getOtp().equals(otp) && existingUser.getOtpExpiry().isAfter(LocalDateTime.now())) {
                existingUser.resetPassword(passwordEncoder.encode(newPassword)); // Update password
                existingUser.setOtp(null); // Clear OTP after successful reset
                existingUser.setOtpExpiry(null);
                existingUser.setIsLoggedIn(false);
                userRepository.save(existingUser);
                return Optional.of(existingUser); // Return the updated User object
            }
        }
        return Optional.empty(); // Return an empty Optional if user is not found or OTP is invalid
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // Generates a 6-digit OTP
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP for Reset Password");
        message.setText("Your OTP for Reset Password on RentWheelZ is: " + otp);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    // Detete Profile API
    public boolean deleteUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User existingUser = user.get();
            userRepository.delete(existingUser); // Delete the user
    
            // Send profile deletion email
            sendProfileDeletionEmail(existingUser.getEmail(), existingUser.getFirstName(), existingUser.getLastName());
    
            return true;
        }
        return false; // User not found
    }

    private void sendProfileDeletionEmail(String email, String firstName, String lastName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Profile Deletion Confirmation - RentWheelZ");
        message.setText("Dear " + firstName + " " + lastName
                + ",\n\nYour profile has been successfully deleted from RentWheelZ. We're sorry to see you go!\n\nIf you ever decide to return, we'll be here to welcome you back and help you get back on the road.\n\nBest wishes,\nThe RentWheelZ Team");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending profile deletion email: " + e.getMessage());
        }
    }

    // Update Profile API
    public User updateProfile(String oldEmail, Map<String, String> updates) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(oldEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Update fields only if provided in the request
            if (updates.containsKey("address")) {
                user.setAddress(updates.get("address"));
            }
            if (updates.containsKey("email")) {
                String newEmail = updates.get("email");

                // If the new email is the same as the old one, no need to check uniqueness
                if (!newEmail.equals(oldEmail)) {
                    // Check if the new email is already registered in the database
                    if (userRepository.findByEmail(newEmail).isPresent()) {
                        throw new Exception("Email already registered.");
                    }
                }
                // Whether the email is the same or different, update the email field
                user.setEmail(newEmail);
            }
            if (updates.containsKey("firstName")) {
                user.setFirstName(updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                user.setLastName(updates.get("lastName"));
            }
            if (updates.containsKey("phoneNumber")) {
                user.setPhoneNumber(updates.get("phoneNumber"));
            }

            // Set modification timestamp
            user.setModifiedAt(LocalDateTime.now());

            // Save the updated user details
            return userRepository.save(user);
        } else {
            throw new Exception("User not found.");
        }
    }

}