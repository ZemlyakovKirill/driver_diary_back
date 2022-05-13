package ru.themlyakov.driverdiary.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.Vehicle;
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