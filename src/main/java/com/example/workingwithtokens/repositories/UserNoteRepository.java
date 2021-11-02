package com.example.workingwithtokens.repositories;

import com.example.workingwithtokens.entities.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote,Long> {

}
