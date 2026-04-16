package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.infra.security.AuthenticatedUserService;
import com.sentinel.sentinel.models.*;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
import com.sentinel.sentinel.repositories.IncidentRepository;
import com.sentinel.sentinel.repositories.SlaRuleRepository;
import com.sentinel.sentinel.specifications.IncidentSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @InjectMocks
    private IncidentService incidentService;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentHistoryRepository incidentHistoryRepository;

    @Mock
    private IncidentHistory incidentHistory;

    @Mock
    private IncidentSpecification incidentSpecification;

    @Mock
    private SlaRuleRepository slaRuleRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;


    private Users user;

    @BeforeEach
    void setup() {
        this.user = new Users(
                1L,
                "User",
                "user@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.ADMIN,
                1
        );
    }

    @Test
    @DisplayName("create incident should create an incident")
    void createIncidentShouldCreateIncident() {

        CreateIncidentDTO dto = new CreateIncidentDTO(
                "Title",
                "Description",
                Severity.HIGH,
                "payment-service"
        );

        SlaRule sla = new SlaRule();
        sla.setDurationHours(2);

        Organization organization = new Organization();
        organization.setId(1L);

        Instant now = Instant.now();

        Incident savedIncident = new Incident(
                "Database connection failure",
                "Service cannot connect to database",
                Severity.HIGH,
                IncidentStatus.OPEN,
                "payment-service",
                now.plus(Duration.ofHours(2)),
                false,
                user,
                organization,
                now,
                now
        );

        savedIncident.setId(10L);

        when(slaRuleRepository.findById(Severity.HIGH.name()))
                .thenReturn(Optional.of(sla));

        when(incidentRepository.save(any(Incident.class)))
                .thenReturn(savedIncident);

        when(incidentHistoryRepository.save(any(IncidentHistory.class)))
                .thenReturn(new IncidentHistory());

        CreatedIncidentDTO result = incidentService.createIncident(dto, user);

        assertNotNull(result);
        assertEquals(10L, result.id());

        verify(incidentRepository, times(1)).save(any(Incident.class));
        verify(incidentHistoryRepository, times(1)).save(any(IncidentHistory.class));

    }
}