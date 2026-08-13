package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident_log.PaginatedIncidentLogsDTO;
import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.IncidentLog;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.IncidentLogRepository;
import com.sentinel.sentinel.exceptions.IncidentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentLogServiceTest {

        @Mock
        private IncidentLogRepository incidentLogRepository;

        @InjectMocks
        private IncidentLogService incidentLogService;

        private AuthenticatedPrincipal principal;

        @BeforeEach
        void setup() {
                Users user = new Users();
                user.setId(1L);
                user.setOrganization(new Organization(1L, "organization", 1));
                user.setRole(com.sentinel.sentinel.enums.Roles.ADMIN);

                principal = new AuthenticatedPrincipal("1", "user@test.com", null,
                                "user", List.of(), 1L, user, null);
        }

        @Test
        @DisplayName("findLogsByParams should return paginated incident logs")
        void findLogsByParamsShouldReturnPaginatedIncidentLogs() {
                IncidentLog incidentLog = new IncidentLog(new Incident(), IncidentLogLevel.ERROR,
                                "message", "stacktrace", "service-name", Instant.now());
                incidentLog.setId(1L);
                incidentLog.getIncidentId().setId(100L);

                Page<IncidentLog> page = new PageImpl<>(List.of(incidentLog), PageRequest.of(0, 10), 1);
                when(incidentLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                PaginatedIncidentLogsDTO result = incidentLogService.findLogsByParams(0, 10, null,
                                IncidentLogLevel.ERROR, null, null, null, null, null, principal);

                assertNotNull(result);
                assertEquals(1, result.totalElements());
                assertEquals(1, result.incidentLogs().size());
                assertEquals(100L, result.incidentLogs().get(0).incidentId());
        }

        @Test
        @DisplayName("generateIncidentLogPdf should return pdf bytes when logs exist")
        void generateIncidentLogPdfShouldReturnPdfBytes() throws Exception {
                IncidentLog incidentLog = new IncidentLog(new Incident(), IncidentLogLevel.INFO,
                                "message", "stacktrace", "service-name", Instant.now());
                incidentLog.setId(1L);
                incidentLog.getIncidentId().setId(200L);

                Page<IncidentLog> page = new PageImpl<>(List.of(incidentLog), PageRequest.of(0, 10), 1);
                when(incidentLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                byte[] pdf = incidentLogService.generateIncidentLogPdf(0, 10, null,
                                IncidentLogLevel.INFO, null, null, null, null, null, principal);

                assertNotNull(pdf);
                assertTrue(pdf.length > 0);
        }

        @Test
        @DisplayName("generateIncidentLogPdf should throw IncidentNotFoundException when no logs are found")
        void generateIncidentLogPdfShouldThrowWhenNoLogsFound() {
                Page<IncidentLog> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
                when(incidentLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                assertThrows(IncidentNotFoundException.class,
                                () -> incidentLogService.generateIncidentLogPdf(0, 10, null,
                                                IncidentLogLevel.INFO, null, null, null, null, null, principal));
        }
}
