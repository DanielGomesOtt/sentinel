package com.sentinel.sentinel.dto.system_integration;


import java.time.Instant;

public record CreatedSystemIntegrationDTO(
    Long id,
    String name,
    String clientId,
    String clientSecretHash,
    Long organizationId,
    Long createdBy,
    boolean active,
    Instant lastUsedAt,
    Instant createdAt,
    Instant updatedAt
) {
}
