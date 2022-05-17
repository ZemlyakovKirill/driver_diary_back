package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.RequestMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.themlyakov.driverdiary.enums.CostTypes;
import ru.themlyakov.driverdiary.enums.SearchTypeMarks;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RequestMarkRepository extends JpaRepository<RequestMark, Long> {


    @Query(value = "select count(user_id) from request_mark where lat=:lat and lon=:lon and user_id=:userId", nativeQuery = true)
    int getMarksByUser(Float lat, Float lon, Long userId);

    RequestMark getRequestMarkByLatAndLonAndType(Float lat, Float lon, SearchTypeMarks type);


    @Query(value = "select * from request_mark " +
            "where sqrt(pow(lat-:lat,2)+pow(lon-:lon,2))<=4.49 " +
            "and type like :type", nativeQuery = true)
    List<RequestMark> getRequestMarksInRadius(Float lat, Float lon, String type);

    @Modifying
    @Transactional
    @Query(value = "delete from request_mark " +
            "where sqrt(pow(lat-:lat,2)+pow(lon-:lon,2))<=4.49 " +
            "and type like :type", nativeQuery = true)
    void deleteInRadius(Float lat, Float lon, String type);
}
