package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select case when max(user_id) is null then 1 else max(user_id)+1 end from users;", nativeQuery = true)
    Long getNewId();

    User findByEmail(String email);

    User findUserByUsername(String username);

}
