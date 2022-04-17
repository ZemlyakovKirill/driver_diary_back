package ru.themlyakov.driverdiary.repositories;

import org.springframework.data.jpa.repository.Query;
import ru.themlyakov.driverdiary.entities.AcceptedMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptedMarkRepository extends JpaRepository<AcceptedMark, Long> {
    @Query(value = "select * from accepted_mark " +
            "where sqrt(pow(lat-:lat,2)+pow(lon-:lon,2))<=4.49 " +
            "and type like :type", nativeQuery = true)
    List<AcceptedMark> getAcceptedMarkByTypeAndLatAndLon(String type,Float lat,Float lon);
}
