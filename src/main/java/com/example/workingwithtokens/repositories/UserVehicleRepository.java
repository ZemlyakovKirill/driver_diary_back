package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.UserVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle,Long> {

}
