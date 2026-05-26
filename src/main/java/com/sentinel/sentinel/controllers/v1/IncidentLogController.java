package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.incident_log.PaginatedIncidentLogsDTO;
import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.services.IncidentLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/v1/incidentLog")
@SecurityRequirement(name = "bearerAuth")
public class IncidentLogController {

    private final IncidentLogService incidentLogService;

    public IncidentLogController(IncidentLogService incidentLogService) {
        this.incidentLogService = incidentLogService;
    }

    @GetMapping
    @Operation(
            summary = "Find incident logs by parameters.",
            description = "Retrieves incident logs based on the provided parameters. "
                    + "The user must be authenticated and belong to the organization."
    )
    public ResponseEntity<PaginatedIncidentLogsDTO> findLogsByParams(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long incidentId,
            @RequestParam(required = false) IncidentLogLevel incidentLogLevel,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal AuthenticatedPrincipal principal
    ) {
        return ResponseEntity.ok(
                incidentLogService.findLogsByParams(
                        page,
                        size,
                        incidentId,
                        incidentLogLevel,
                        message,
                        serviceName,
                        from,
                        to,
                        userId,
                        principal
                )
        );
    }
}
