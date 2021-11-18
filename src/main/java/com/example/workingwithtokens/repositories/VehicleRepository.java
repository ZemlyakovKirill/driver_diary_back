package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    @Transactional
    void deleteById(Long id);
}
