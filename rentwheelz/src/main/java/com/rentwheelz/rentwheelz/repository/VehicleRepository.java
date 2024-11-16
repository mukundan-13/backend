package com.rentwheelz.rentwheelz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rentwheelz.rentwheelz.model.Vehicle;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
}