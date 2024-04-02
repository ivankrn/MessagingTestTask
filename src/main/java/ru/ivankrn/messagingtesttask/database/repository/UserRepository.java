package ru.ivankrn.messagingtesttask.database.repository;

import org.springframework.data.repository.CrudRepository;
import ru.ivankrn.messagingtesttask.database.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
