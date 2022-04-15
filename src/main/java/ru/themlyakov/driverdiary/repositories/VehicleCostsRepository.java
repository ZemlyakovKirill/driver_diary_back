package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.VehicleCosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleCostsRepository extends JpaRepository<VehicleCosts,Long> {

}
