package ru.themlyakov.driverdiary.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.themlyakov.driverdiary.entities.VehicleCosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VehicleCostsRepository extends JpaRepository<VehicleCosts,Long> {


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM vehicle_costs WHERE cost_id=:id",nativeQuery = true)
    void delete(Long id);
}
