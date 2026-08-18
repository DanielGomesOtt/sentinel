package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long>, JpaSpecificationExecutor<IncidentHistory> {
}
