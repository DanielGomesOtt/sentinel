package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<UserDetails> findByEmailAndStatus(String email, int status);
}
