package com.sentinel.sentinel.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel.dto.incident_log.PaginatedIncidentLogsDTO;
import com.sentinel.sentinel.dto.incident_log.CreatedIncidentLogDTO;
import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.infra.security.SecurityFilter;
import com.sentinel.sentinel.services.IncidentLogService;
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

import java.security.Security;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncidentLogControllerTest {

    @MockitoBean
    private IncidentLogService incidentLogService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("find logs by params should return paginated logs")
    void findLogsByParamsShouldReturnPaginatedLogs() throws Exception {
        CreatedIncidentLogDTO logDto = new CreatedIncidentLogDTO(1L, IncidentLogLevel.ERROR,
                "error message", "stacktrace", "service-name");
        PaginatedIncidentLogsDTO paginated = new PaginatedIncidentLogsDTO(
                List.of(logDto),
                true,
                true,
                0,
                1,
                10,
                1,
                1
        );

        when(incidentLogService.findLogsByParams(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(paginated);

        mockMvc.perform(get("/v1/incidentLog")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentLogs[0].incidentId").value(1))
                .andExpect(jsonPath("$.incidentLogs[0].message").value("error message"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("generate pdf should return pdf bytes and headers")
    void generatePdfShouldReturnPdfBytes() throws Exception {
        byte[] pdf = "fake-pdf-bytes".getBytes();

        when(incidentLogService.generateIncidentLogPdf(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pdf);

        mockMvc.perform(get("/v1/incidentLog/pdf")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=incident_log.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }
}
