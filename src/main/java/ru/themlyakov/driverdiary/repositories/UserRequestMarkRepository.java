package ru.themlyakov.driverdiary.repositories;

import org.springframework.data.jpa.repository.Query;
import ru.themlyakov.driverdiary.entities.RequestMark;
import ru.themlyakov.driverdiary.entities.UserRequestMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRequestMarkRepository extends JpaRepository<UserRequestMark,Long> {


    @Query(value = "select count(*) from user_request_mark where request_id=:rid and is_truth is true " +
            "union " +
            "select count(*) from user_request_mark where request_id=:rid and is_truth is false",nativeQuery = true)
    List<Integer> countChoices(Long rid);
}
