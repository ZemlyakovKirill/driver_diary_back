package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.UserRequestMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestMarkRepository extends JpaRepository<UserRequestMark,Long> {
}
