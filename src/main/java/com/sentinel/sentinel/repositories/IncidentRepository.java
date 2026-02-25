package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
}
