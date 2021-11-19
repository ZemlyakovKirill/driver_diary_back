package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Modifying
    void deleteVehicleById(Long id);
}