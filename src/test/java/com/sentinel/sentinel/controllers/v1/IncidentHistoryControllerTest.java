package com.sentinel.sentinel.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel.dto.incident_history.PaginatedIncidentHistoriesDTO;
import com.sentinel.sentinel.dto.incident_history.CreatedIncidentHistoryDTO;
import com.sentinel.sentinel.infra.security.SecurityFilter;
import com.sentinel.sentinel.services.IncidentHistoryService;
import com.sentinel.sentinel.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncidentHistoryControllerTest {

    @MockitoBean
    private IncidentHistoryService incidentHistoryService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("find histories by params should return paginated histories")
    void findHistoriesByParamsShouldReturnPaginatedHistories() throws Exception {
        CreatedIncidentHistoryDTO historyDto = new CreatedIncidentHistoryDTO(1L, 2L,
                "OPEN", "CLOSED", "update", 1L, null, "2026-07-27 00:00:00");
        PaginatedIncidentHistoriesDTO paginated = new PaginatedIncidentHistoriesDTO(
                List.of(historyDto),
                true,
                true,
                0,
                1,
                10,
                1,
                1
        );

        when(incidentHistoryService.findHistoriesByParams(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(paginated);

        mockMvc.perform(get("/v1/incidentHistory")
                        .param("page", "0")
                        .param("size", "10")
                        .param("incidentId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentHistories[0].id").value(1))
                .andExpect(jsonPath("$.incidentHistories[0].incidentId").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("generate pdf should return pdf bytes and headers")
    void generatePdfShouldReturnPdfBytes() throws Exception {
        byte[] pdf = "fake-pdf-bytes".getBytes();

        when(incidentHistoryService.generateIncidentHistoryPdf(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pdf);

        mockMvc.perform(get("/v1/incidentHistory/pdf")
                        .param("page", "0")
                        .param("size", "10")
                        .param("incidentId", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=incident_history.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }
}
