package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Transactional
    @Modifying
    void deleteVehicleById(Long id);
}