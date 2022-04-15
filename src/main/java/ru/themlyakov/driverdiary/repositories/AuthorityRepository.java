package ru.themlyakov.driverdiary.repositories;

import ru.themlyakov.driverdiary.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    public Authority findAuthoritiesByAuthority(String authortity);
}
