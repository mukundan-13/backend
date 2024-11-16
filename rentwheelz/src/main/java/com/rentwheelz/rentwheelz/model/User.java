package com.rentwheelz.rentwheelz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;  // New field for first name
    private String lastName;   // New field for last name
    private String email;      // Same as before
    private String address;    // New field for address
    private String password;   // Same as before, but will be hashed
    private String role = "Customer"; // New field with default value as "Customer"
    private String phoneNumber; // New field for phone number
    private LocalDateTime createdAt;  // New field for timestamp of registration
    private LocalDateTime modifiedAt; // New field for timestamp of last modification
    private boolean isLoggedIn = false; // New field to track login status

    // New fields for OTP and expiration time
    private String otp;
    private LocalDateTime otpExpiry;

    @Transient
    private String confirmPassword;  // Same as before, will not be persisted

    // Default constructor
    // public User() {
    // }

    // // Constructor with parameters
    // public User(String firstName, String lastName, String email, String address, String password, String confirmPassword, String role, String phoneNumber) {
    //     this.firstName = firstName;
    //     this.lastName = lastName;
    //     this.email = email;
    //     this.address = address;
    //     this.password = password;
    //     this.confirmPassword = confirmPassword;
    //     this.role = role;
    //     this.phoneNumber = phoneNumber;
    //     this.createdAt = LocalDateTime.now();
    //     this.modifiedAt = LocalDateTime.now();
    // }

    // Getter and setter methods for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // OTP-related getter and setter methods
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    // Method to reset the password
    public void resetPassword(String newPassword) {
        this.password = newPassword; // Update the password
        this.modifiedAt = LocalDateTime.now(); // Update the modifiedAt timestamp
    }
}