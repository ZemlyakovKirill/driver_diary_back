package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.AcceptedMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptedMarkRepository extends JpaRepository<AcceptedMark, Long> {
    
}
