package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNewsRepository extends JpaRepository<News,Long> {
}
