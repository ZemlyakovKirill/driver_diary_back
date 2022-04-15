package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.AcceptedMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptedMarkRepository extends JpaRepository<AcceptedMark, Long> {
    
}
