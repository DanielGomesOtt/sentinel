package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {
}
