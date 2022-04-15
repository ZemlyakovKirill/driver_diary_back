package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote,Long> {

}
