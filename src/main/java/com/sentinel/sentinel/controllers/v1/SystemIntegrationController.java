package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.system_integration.CreateSystemIntegrationDTO;
import com.sentinel.sentinel.dto.system_integration.CreatedSystemIntegrationDTO;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.services.SystemIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/systemIntegration")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "System Integration", description = "Here are the requests used to perform the functionality related to system integrations.")
public class SystemIntegrationController {

    private final SystemIntegrationService systemIntegrationService;

    public SystemIntegrationController(SystemIntegrationService systemIntegrationService) {
        this.systemIntegrationService = systemIntegrationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a system integration",
            description = "Creates a new system integration and generates the required credentials for external system communication."
    )
    public ResponseEntity<CreatedSystemIntegrationDTO> create (@RequestBody @Valid CreateSystemIntegrationDTO data,
                                                               @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        CreatedSystemIntegrationDTO createdSystemIntegration = systemIntegrationService.createdSystemIntegration(
                data, principal);

        URI uri = URI.create("/v1/systemIntegration/" + createdSystemIntegration.id());

        return ResponseEntity.created(uri).body(createdSystemIntegration);
    }


}
