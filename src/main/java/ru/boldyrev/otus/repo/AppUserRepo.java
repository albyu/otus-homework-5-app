package ru.boldyrev.otus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.boldyrev.otus.model.AppUser;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByUsernameAndPassword(String username, String password);

}
