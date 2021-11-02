package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.AcceptedMark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedMarkRepository extends JpaRepository<AcceptedMark, Long> {
    
}
