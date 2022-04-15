package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.UserVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle,Long> {

}
