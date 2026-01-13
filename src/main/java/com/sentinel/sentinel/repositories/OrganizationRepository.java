package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
