package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.system_integration.CreateSystemIntegrationDTO;
import com.sentinel.sentinel.dto.system_integration.CreatedSystemIntegrationDTO;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.models.SystemIntegration;
import com.sentinel.sentinel.repositories.SystemIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemIntegrationServiceTest {

    @Mock
    private SystemIntegrationRepository systemIntegrationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SystemIntegrationService systemIntegrationService;

    private AuthenticatedPrincipal principal;

    @BeforeEach
    void setup() {
        Users user = new Users();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@test.com");
        user.setOrganization(new Organization(1L, "organization", 1));

        principal = new AuthenticatedPrincipal("1", "user@test.com", null,
                "user", List.of(), 1L, user, null);
    }

    @Test
    @DisplayName("createdSystemIntegration should save and return created DTO")
    void createdSystemIntegrationShouldReturnCreatedDto() {
        CreateSystemIntegrationDTO request = new CreateSystemIntegrationDTO("integration-name");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-secret");

        SystemIntegration saved = new SystemIntegration(
                1L,
                "integration-name",
                "generated-client-id",
                "encoded-secret",
                principal.getUser().getOrganization(),
                principal.getUser(),
                true,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        when(systemIntegrationRepository.save(any(SystemIntegration.class))).thenReturn(saved);

        CreatedSystemIntegrationDTO result = systemIntegrationService.createdSystemIntegration(request, principal);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("integration-name", result.name());
        assertNotNull(result.clientSecretHash());
        assertEquals(1L, result.organizationId());
        assertEquals(1L, result.createdBy());

        ArgumentCaptor<SystemIntegration> captor = ArgumentCaptor.forClass(SystemIntegration.class);
        verify(systemIntegrationRepository).save(captor.capture());

        SystemIntegration captured = captor.getValue();
        assertEquals("integration-name", captured.getName());
        assertEquals(principal.getUser(), captured.getCreatedBy());
        assertTrue(captured.isActive());
    }
}
