package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.dto.incident.PaginatedIncidentsDTO;
import com.sentinel.sentinel.dto.incident.UpdateIncidentDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.exceptions.IncidentAlreadyClosedException;
import com.sentinel.sentinel.exceptions.IncidentNotFoundException;
import com.sentinel.sentinel.exceptions.UserNotAuthenticatedException;
import com.sentinel.sentinel.models.*;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
import com.sentinel.sentinel.repositories.IncidentRepository;
import com.sentinel.sentinel.repositories.SlaRuleRepository;
import com.sentinel.sentinel.specifications.IncidentSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
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

    private Users user;

    private Users user2;

    private Users user3;

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

        this.user2 = new Users(
                2L,
                "User2",
                "user2@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.USER,
                1
        );

        this.user3 = new Users(
                3L,
                "User3",
                "user3@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.TECH,
                1
        );
    }

    @Test
    @DisplayName("should create incident with correct SLA and history")
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
                "Title",
                "Description",
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
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreatedIncidentDTO result = incidentService.createIncident(dto, user);

        assertNotNull(result);
        assertEquals(10L, result.id());

        ArgumentCaptor<Incident> incidentCaptor =
                ArgumentCaptor.forClass(Incident.class);

        verify(incidentRepository).save(incidentCaptor.capture());

        Incident capturedIncident = incidentCaptor.getValue();

        assertEquals("Title", capturedIncident.getTitle());
        assertEquals("Description", capturedIncident.getDescription());
        assertEquals(Severity.HIGH, capturedIncident.getSeverity());
        assertEquals(IncidentStatus.OPEN, capturedIncident.getIncidentStatus());
        assertEquals(user, capturedIncident.getCreatedBy());

        ArgumentCaptor<IncidentHistory> historyCaptor =
                ArgumentCaptor.forClass(IncidentHistory.class);

        verify(incidentHistoryRepository).save(historyCaptor.capture());

        IncidentHistory history = historyCaptor.getValue();

        assertEquals("create incident", history.getAction());
        assertEquals(IncidentStatus.OPEN.name(), history.getNewStatus());
    }

    @Test
    @DisplayName("create incident should throw UserNotAuthenticatedException")
    void createIncidentShouldThrowUserNotAuthenticatedException() {

        CreateIncidentDTO dto = new CreateIncidentDTO(
                "Title",
                "Description",
                Severity.HIGH,
                "payment-service"
        );

        assertThrows(UserNotAuthenticatedException.class, () -> {
            incidentService.createIncident(dto, null);
        });

    }

    @Test
    @DisplayName("Should find an incident by id as ADMIN user")
    void getIncidentByIdAsAdminUserShouldReturnIncident() {

        Incident foundIncident = new Incident(
                1L,
                "Database connection failure",
                "Service cannot connect to database",
                Severity.HIGH,
                IncidentStatus.OPEN,
                "payment-service",
                Instant.now(),
                false,
                user,
                user.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        when(incidentRepository.findByIdAndCreatedByOrganization(1L, user.getOrganization()))
                .thenReturn(Optional.of(foundIncident));

        CreatedIncidentDTO result = incidentService.getIncidentById(1L, user);

        assertEquals(1L, result.id());

        verify(incidentRepository).findByIdAndCreatedByOrganization(1L, user.getOrganization());
    }

    @Test
    @DisplayName("Should find an incident by id as basic user")
    void getIncidentByIdAsBasicUserShouldReturnIncident() {

        Incident foundIncident = new Incident(
                1L,
                "Database connection failure",
                "Service cannot connect to database",
                Severity.HIGH,
                IncidentStatus.OPEN,
                "payment-service",
                Instant.now(),
                false,
                user2,
                user2.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        when(incidentRepository.
                findByIdAndCreatedByOrganizationAndCreatedBy(1L, user2.getOrganization(), user2))
                .thenReturn(Optional.of(foundIncident));

        CreatedIncidentDTO result = incidentService.getIncidentById(1L, user2);

        assertEquals(1L, result.id());

        verify(incidentRepository).
                findByIdAndCreatedByOrganizationAndCreatedBy(1L, user2.getOrganization(), user2);
    }

    @Test
    @DisplayName("get incident by id as admin should throw IncidentNotFoundException")
    void getIncidentByIdAsAdminUserShouldThrowIncidentNotFoundException() {
        when(incidentRepository.findByIdAndCreatedByOrganization(1L, user.getOrganization()))
                .thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> {
            incidentService.getIncidentById(1L, user);
        });

        verify(incidentRepository).findByIdAndCreatedByOrganization(1L, user.getOrganization());

    }

    @Test
    @DisplayName("get incident by id as basic user should throw IncidentNotFoundException")
    void getIncidentByIdAsBasicUserShouldThrowIncidentNotFoundException() {
        when(incidentRepository
                .findByIdAndCreatedByOrganizationAndCreatedBy(1L, user2.getOrganization(), user2))
                .thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> {
            incidentService.getIncidentById(1L, user2);
        });

        verify(incidentRepository).
                findByIdAndCreatedByOrganizationAndCreatedBy(1L, user2.getOrganization(), user2);
    }

    @Test
    @DisplayName("get incident by id should throw UserNotAuthenticatedException.")
    void getIncidentByIdShouldThrowUserNotFoundException() {
        assertThrows(UserNotAuthenticatedException.class, () -> {
            incidentService.getIncidentById(1L, null);
        });
    }

    @Test
    @DisplayName("find all should return a list of incidents")
    void findAllShouldReturnListIncidents() {

        Incident incident = new Incident(
                1L,
                "Database connection failure",
                "Service cannot connect to database",
                Severity.HIGH,
                IncidentStatus.OPEN,
                "payment-service",
                Instant.now(),
                false,
                user,
                user.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        Page<Incident> foundIncidents =  new PageImpl<>(
                List.of(incident),
                PageRequest.of(0, 10),
                1
        );

        when(incidentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(foundIncidents);

        PaginatedIncidentsDTO result = incidentService.findAll(0, 10, null, null, null,
                null, null, null, null, user);

        assertNotNull(result);
        assertEquals(1L, result.incidents().getFirst().id());
    }

    @Test
    @DisplayName("should update incident and create history entry when data is valid")
    void updateIncidentShouldUpdateIncidentAndCreateHistory() {
        UpdateIncidentDTO dto = new UpdateIncidentDTO(
                1L,
                "Updated title",
                "Updated description",
                Severity.HIGH,
                "payment-service",
                IncidentStatus.OPEN
        );

        Incident incident = new Incident(
                1L,
                "Old title",
                "Old description",
                Severity.LOW,
                IncidentStatus.OPEN,
                "old-service",
                Instant.now(),
                false,
                user,
                user.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        when(incidentRepository.findByIdAndCreatedByOrganization(1L, user.getOrganization()))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(incidentHistoryRepository.save(any(IncidentHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateIncidentDTO result = incidentService.updateIncident(dto, user);

        assertNotNull(result);
        assertEquals("Updated title", result.title());
        assertEquals("Updated description", result.description());
        assertEquals(Severity.HIGH, result.severity());
        assertEquals("payment-service", result.serviceName());
        assertEquals(IncidentStatus.OPEN, result.incidentStatus());


        verify(incidentRepository).save(any(Incident.class));

        ArgumentCaptor<IncidentHistory> historyCaptor =
                ArgumentCaptor.forClass(IncidentHistory.class);

        verify(incidentHistoryRepository).save(historyCaptor.capture());

        IncidentHistory savedHistory = historyCaptor.getValue();

        assertEquals("update incident", savedHistory.getAction());
        assertEquals(IncidentStatus.OPEN.name(), savedHistory.getNewStatus());
    }

    @Test
    @DisplayName("update incident should throw exception when incident is not found")
    void updateIncidentShouldThrowExceptionIncidentNotFound() {
        UpdateIncidentDTO dto = new UpdateIncidentDTO(
                1L,
                "Updated title",
                "Updated description",
                Severity.HIGH,
                "payment-service",
                IncidentStatus.OPEN
        );

        when(incidentRepository.findByIdAndCreatedByOrganization(dto.incidentId(), user.getOrganization()))
                .thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> {
           incidentService.updateIncident(dto, user);
        });
    }

    @Test
    @DisplayName("update incident should not allow TECH to close an incident")
    void updateIncidentShouldNotAllowTechCloseIncident() {
        UpdateIncidentDTO dto = new UpdateIncidentDTO(
                1L,
                "Updated title",
                "Updated description",
                Severity.HIGH,
                "payment-service",
                IncidentStatus.CLOSED
        );

        Incident incident = new Incident(
                1L,
                "Old title",
                "Old description",
                Severity.LOW,
                IncidentStatus.OPEN,
                "old-service",
                Instant.now(),
                false,
                user3,
                user3.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        when(incidentRepository.findByIdAndCreatedByOrganization(1L, user.getOrganization()))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(incidentHistoryRepository.save(any(IncidentHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateIncidentDTO result = incidentService.updateIncident(dto, user3);

        assertNotNull(result);
        assertEquals("Updated title", result.title());
        assertEquals("Updated description", result.description());
        assertEquals(Severity.HIGH, result.severity());
        assertEquals("payment-service", result.serviceName());
        assertEquals(IncidentStatus.OPEN, result.incidentStatus());


        verify(incidentRepository).save(any(Incident.class));

        ArgumentCaptor<IncidentHistory> historyCaptor =
                ArgumentCaptor.forClass(IncidentHistory.class);

        verify(incidentHistoryRepository).save(historyCaptor.capture());

        IncidentHistory savedHistory = historyCaptor.getValue();

        assertEquals("update incident", savedHistory.getAction());
        assertEquals(IncidentStatus.OPEN.name(), savedHistory.getNewStatus());
    }

    @Test
    @DisplayName("update incident should not allow updates if incident is already CLOSED")
    void updateIncidentShouldThrowIncidentAlreadyClosedException() {
        UpdateIncidentDTO dto = new UpdateIncidentDTO(
                1L,
                "Updated title",
                "Updated description",
                Severity.HIGH,
                "payment-service",
                IncidentStatus.OPEN
        );

        Incident incident = new Incident(
                1L,
                "Old title",
                "Old description",
                Severity.LOW,
                IncidentStatus.CLOSED,
                "old-service",
                Instant.now(),
                false,
                user,
                user.getOrganization(),
                Instant.now(),
                Instant.now()
        );

        when(incidentRepository.findByIdAndCreatedByOrganization(1L, user.getOrganization()))
                .thenReturn(Optional.of(incident));

        assertThrows(IncidentAlreadyClosedException.class, () -> {
            incidentService.updateIncident(dto, user);
        });
    }
}

