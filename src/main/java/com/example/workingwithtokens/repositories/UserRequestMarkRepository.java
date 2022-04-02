package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.UserRequestMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestMarkRepository extends JpaRepository<UserRequestMark,Long> {
}
