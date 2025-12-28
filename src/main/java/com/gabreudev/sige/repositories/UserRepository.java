package com.gabreudev.sige.repositories;

import com.gabreudev.sige.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByCoren(String coren);
}
