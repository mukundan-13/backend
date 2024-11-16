package com.rentwheelz.rentwheelz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rentwheelz.rentwheelz.model.Booking;

import java.time.LocalDate;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndEndDateBefore(String status, LocalDate endDate);

    List<Booking> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND b.status != 'Canceled' AND " +
    "(b.startDate < :endDate AND b.endDate > :startDate)")
    List<Booking> findConflictingBookings(
     @Param("vehicleId") Long vehicleId,
     @Param("startDate") LocalDate startDate,
     @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND b.status = :status")
    List<Booking> findByVehicleIdAndStatus(@Param("vehicleId") Long vehicleId, @Param("status") String status);
     
    
}