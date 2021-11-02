package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNewsRepository extends JpaRepository<News,Long> {
}
