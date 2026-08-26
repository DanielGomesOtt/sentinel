package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident_history.PaginatedIncidentHistoriesDTO;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.IncidentHistory;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
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
class IncidentHistoryServiceTest {

        @Mock
        private IncidentHistoryRepository incidentHistoryRepository;

        @InjectMocks
        private IncidentHistoryService incidentHistoryService;

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
        @DisplayName("findHistoriesByParams should return paginated incident histories")
        void findHistoriesByParamsShouldReturnPaginatedIncidentHistories() {
                IncidentHistory incidentHistory = new IncidentHistory(new Incident(), "OPEN", "CLOSED",
                                "change", new Users(), Instant.now());
                incidentHistory.setId(1L);
                incidentHistory.getIncidentId().setId(100L);

                Page<IncidentHistory> page = new PageImpl<>(List.of(incidentHistory), PageRequest.of(0, 10), 1);
                when(incidentHistoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                PaginatedIncidentHistoriesDTO result = incidentHistoryService.findHistoriesByParams(0, 10, 100L,
                                "OPEN", "CLOSED", "change", Instant.parse("2026-07-27T00:00:00Z"), Instant.parse("2026-07-27T00:00:00Z"), null,
                                principal);

                assertNotNull(result);
                assertEquals(1, result.totalElements());
                assertEquals(1, result.incidentHistories().size());
                assertEquals(1L, result.incidentHistories().get(0).id());
        }

        @Test
        @DisplayName("generateIncidentHistoryPdf should return pdf bytes when histories exist")
        void generateIncidentHistoryPdfShouldReturnPdfBytes() throws Exception {
                IncidentHistory incidentHistory = new IncidentHistory(new Incident(), "OPEN", "CLOSED",
                                "change", new Users(), Instant.now());
                incidentHistory.setId(1L);
                incidentHistory.getIncidentId().setId(100L);

                Page<IncidentHistory> page = new PageImpl<>(List.of(incidentHistory), PageRequest.of(0, 10), 1);
                when(incidentHistoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                byte[] pdf = incidentHistoryService.generateIncidentHistoryPdf(0, 10, 100L,
                                "OPEN", "CLOSED", "change", Instant.parse("2026-07-27T00:00:00Z"), Instant.parse("2026-07-27T00:00:00Z"), null,
                                principal);

                assertNotNull(pdf);
                assertTrue(pdf.length > 0);
        }

        @Test
        @DisplayName("generateIncidentHistoryPdf should throw IncidentNotFoundException when no histories are found")
        void generateIncidentHistoryPdfShouldThrowWhenNoHistoriesFound() {
                Page<IncidentHistory> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
                when(incidentHistoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

                assertThrows(IncidentNotFoundException.class,
                                () -> incidentHistoryService.generateIncidentHistoryPdf(0, 10, 100L,
                                                "OPEN", "CLOSED", "change", Instant.parse("2026-07-27T00:00:00Z"),
                                                Instant.parse("2026-07-27T00:00:00Z"), null, principal));
        }
}
