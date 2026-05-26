package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.IncidentHistory;
import com.sentinel.sentinel.models.IncidentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncidentLogRepository extends JpaRepository<IncidentLog, Long>, JpaSpecificationExecutor<IncidentLog> {
}
