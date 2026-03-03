package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.SlaRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaRuleRepository extends JpaRepository<SlaRule, String> {
}
