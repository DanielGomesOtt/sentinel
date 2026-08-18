package com.sentinel.sentinel.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel.dto.system_integration.CreateSystemIntegrationDTO;
import com.sentinel.sentinel.dto.system_integration.CreatedSystemIntegrationDTO;
import com.sentinel.sentinel.infra.security.SecurityFilter;
import com.sentinel.sentinel.services.SystemIntegrationService;
import com.sentinel.sentinel.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemIntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class SystemIntegrationControllerTest {

    @MockitoBean
    private SystemIntegrationService systemIntegrationService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("create should return 201 and created system integration")
    void createShouldReturnCreatedSystemIntegration() throws Exception {
        CreateSystemIntegrationDTO request = new CreateSystemIntegrationDTO("external system");
        CreatedSystemIntegrationDTO created = new CreatedSystemIntegrationDTO(
                1L,
                "external system",
                "clientId",
                "secret-value",
                1L,
                1L,
                true,
                null,
                null,
                null
        );

        when(systemIntegrationService.createdSystemIntegration(any(CreateSystemIntegrationDTO.class), any()))
                .thenReturn(created);

        mockMvc.perform(post("/v1/systemIntegration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/systemIntegration/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("external system"))
                .andExpect(jsonPath("$.clientId").value("clientId"));
    }
}
