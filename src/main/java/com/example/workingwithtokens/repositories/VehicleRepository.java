package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    @Query(value = "delete from vehicles where vehicle_id=:id",nativeQuery = true)
    int delete(Long id);
}
