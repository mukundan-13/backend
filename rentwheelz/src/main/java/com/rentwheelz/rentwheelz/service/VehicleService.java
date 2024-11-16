package com.rentwheelz.rentwheelz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.rentwheelz.rentwheelz.model.Vehicle;
import com.rentwheelz.rentwheelz.repository.VehicleRepository;

@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;

   
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
}