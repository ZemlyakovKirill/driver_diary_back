package ru.themlyakov.driverdiary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.themlyakov.driverdiary.entities.News;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNewsRepository extends JpaRepository<News,Long> {

}
