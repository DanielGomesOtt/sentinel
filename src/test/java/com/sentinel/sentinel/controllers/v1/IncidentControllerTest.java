package com.sentinel.sentinel.controllers.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.dto.incident.PaginatedIncidentsDTO;
import com.sentinel.sentinel.dto.incident.UpdateIncidentDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
import com.sentinel.sentinel.repositories.IncidentRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import com.sentinel.sentinel.services.IncidentService;
import com.sentinel.sentinel.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncidentControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    @MockitoBean
    private IncidentRepository incidentRepository;

    @MockitoBean
    private IncidentHistoryRepository incidentHistoryRepository;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsersRepository usersRepository;

    @Test
    @DisplayName("create manually should return a created incident with uri")
    @WithMockUser
    void createManuallyShouldReturnCreatedIncidentWithURI() throws Exception {
        CreateIncidentDTO data = new CreateIncidentDTO("title", "description", Severity.HIGH,
                "service name");



        CreatedIncidentDTO createdIncident = new CreatedIncidentDTO(1L, "title", "description",
                Severity.HIGH.name(), IncidentStatus.OPEN.name(), "service name",
                Instant.now().plus(Duration.ofHours(2)).toString(), 1L);


        when(incidentService.createIncident(any(), any())).thenReturn(createdIncident);

        mockMvc.perform(post("/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/incidents/1"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("find incident by id should return an incident")
    @WithMockUser
    void findIncidentByIdShouldReturnIncident() throws Exception {
        CreatedIncidentDTO createdIncident = new CreatedIncidentDTO(1L, "title", "description",
                Severity.HIGH.name(), IncidentStatus.OPEN.name(), "service name",
                Instant.now().plus(Duration.ofHours(2)).toString(), 1L);

        when(incidentService.getIncidentById(any(), any())).thenReturn(createdIncident);

        mockMvc.perform(get("/v1/incidents/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("find all should return paginated incidents")
    @WithMockUser
    void findAllShouldReturnPaginatedIncidents() throws Exception {

        Users user = new Users(
                1L,
                "User",
                "user@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.ADMIN,
                1
        );

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

        CreatedIncidentDTO formattedIncident = new CreatedIncidentDTO(incident);


        Page<Incident> foundIncidents =  new PageImpl<>(
                List.of(incident),
                PageRequest.of(0, 10),
                1
        );

        PaginatedIncidentsDTO paginatedIncidents = new PaginatedIncidentsDTO(
                List.of(formattedIncident),
                foundIncidents.isFirst(),
                foundIncidents.isLast(),
                foundIncidents.getNumber(),
                foundIncidents.getNumberOfElements(),
                foundIncidents.getSize(),
                foundIncidents.getTotalElements(),
                foundIncidents.getTotalPages()
        );

        when(incidentService.findAll(
                anyInt(),
                anyInt(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(paginatedIncidents);

        mockMvc.perform(get("/v1/incidents")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents[0].id").value(1L))
                .andExpect(jsonPath("$.incidents[0].title").value("Database connection failure"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("update manually should return an updated incident")
    @WithMockUser
    void updateManuallyShouldReturnUpdatedIncident() throws Exception {
        UpdateIncidentDTO data = new UpdateIncidentDTO(1L, "title", "description",
                Severity.LOW, "serviceName", IncidentStatus.OPEN);

        when(incidentService.updateIncident(eq(data), any())).thenReturn(data);

        mockMvc.perform(put("/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentId").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.severity").value("LOW"))
                .andExpect(jsonPath("$.serviceName").value("serviceName"))
                .andExpect(jsonPath("$.incidentStatus").value("OPEN"));;



    }
}