package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.SystemIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemIntegrationRepository extends JpaRepository<SystemIntegration, Long> {

    Optional<SystemIntegration> findByClientId(String clientId);
}
