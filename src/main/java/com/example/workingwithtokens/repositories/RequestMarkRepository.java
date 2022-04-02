package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.RequestMark;
import com.example.workingwithtokens.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RequestMarkRepository extends JpaRepository<RequestMark, Long> {


    @Query(value = "select count(user_id) from request_mark where lat=:lat and lon=:lon and user_id=:userId", nativeQuery = true)
    int getMarksByUser(Float lat, Float lon, Long userId);

    RequestMark getRequestMarkByLatAndLonAndType(Float lat, Float lon, String type);


    @Query(value = "select * from request_mark " +
            "where sqrt(pow(lat-:lat,2)+pow(lon-:lon,2))<=4.49 " +
            "and type like :type group by user_id;", nativeQuery = true)
    List<RequestMark> getRequestMarksInRadius(Float lat, Float lon, String type);

    @Modifying
    @Transactional
    @Query(value = "delete from request_mark " +
            "where sqrt(pow(lat-:lat,2)+pow(lon-:lon,2))<=4.49 " +
            "and type like :type", nativeQuery = true)
    void deleteInRadius(Float lat, Float lon, String type);
}
