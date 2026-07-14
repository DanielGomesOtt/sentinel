package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.ForgotPasswordToken;
import com.sentinel.sentinel.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {
    Optional<ForgotPasswordToken> findByUserAndUsed(Users user, boolean used);
}
