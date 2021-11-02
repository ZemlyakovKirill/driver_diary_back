package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.VehicleCosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleCostsRepository extends JpaRepository<VehicleCosts,Long> {

}
