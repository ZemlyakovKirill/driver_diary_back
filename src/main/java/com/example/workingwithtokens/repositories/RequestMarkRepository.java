package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.RequestMark;
import com.example.workingwithtokens.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestMarkRepository extends JpaRepository<RequestMark,Long> {
    @Query(value = "select count(user_id) from request_mark where lat=:lat and lon=:lon and user_id=:userId",nativeQuery = true)
    int getMarksByUser(Float lat,Float lon,Long userId);
}
