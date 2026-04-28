package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.system_integration.CreateSystemIntegrationDTO;
import com.sentinel.sentinel.dto.system_integration.CreatedSystemIntegrationDTO;
import com.sentinel.sentinel.models.SystemIntegration;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.SystemIntegrationRepository;
import com.sentinel.sentinel.utils.SecretGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SystemIntegrationService {

    private final SystemIntegrationRepository systemIntegrationRepository;
    private final PasswordEncoder passwordEncoder;
    public SystemIntegrationService(SystemIntegrationRepository systemIntegrationRepository,
                                    PasswordEncoder passwordEncoder) {
        this.systemIntegrationRepository = systemIntegrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreatedSystemIntegrationDTO createdSystemIntegration(CreateSystemIntegrationDTO data, Users user) {
        String clientId = UUID.randomUUID().toString();

        String clientSecret = SecretGenerator.generate();
        String clientSecretEncoded = passwordEncoder.encode(clientSecret);

        SystemIntegration newSystemIntegration = new SystemIntegration(null, data.name(), clientId,
                clientSecretEncoded, user.getOrganization(), user, true, Instant.now(), Instant.now(), Instant.now());

        SystemIntegration createdSystemIntegration = systemIntegrationRepository.save(newSystemIntegration);


        return new CreatedSystemIntegrationDTO(createdSystemIntegration.getId(), createdSystemIntegration.getName(),
                createdSystemIntegration.getClientId(), clientSecret, createdSystemIntegration.getOrganizationId().getId(),
                createdSystemIntegration.getCreatedBy().getId(), createdSystemIntegration.isActive(),
                createdSystemIntegration.getLastUsedAt(), createdSystemIntegration.getCreatedAt(),
                createdSystemIntegration.getUpdatedAt());
    }
}
