package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.IncidentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentLogRepository extends JpaRepository<IncidentLog, Long> {
}
