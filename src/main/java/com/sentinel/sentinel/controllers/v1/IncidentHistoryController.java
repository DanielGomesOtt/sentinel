package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.incident_history.PaginatedIncidentHistoriesDTO;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.services.IncidentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/incidentHistory")
@SecurityRequirement(name = "bearerAuth")
public class IncidentHistoryController {

    @Autowired
    private IncidentHistoryService incidentHistoryService;

    @GetMapping
    @Operation(
            summary = "Find incident histories by parameters.",
            description = "Retrieves incident histories based on the provided parameters. "
                    + "The user must be authenticated and belong to the organization."
    )
    public ResponseEntity<PaginatedIncidentHistoriesDTO> findHistoriesByParams(@RequestParam(required = true) int page,
                                                                               @RequestParam(required = true) int size,
                                                                               @RequestParam(required = true) Long incidentId,
                                                                               @RequestParam(required = false) String newStatus,
                                                                               @RequestParam(required = false) String previousStatus,
                                                                               @RequestParam(required = false) String action,
                                                                               @RequestParam(required = false) String from,
                                                                               @RequestParam(required = false) String to,
                                                                               @RequestParam(required = false) Long userId,
                                                                               @AuthenticationPrincipal AuthenticatedPrincipal principal ) {
        return ResponseEntity.ok(incidentHistoryService.findHistoriesByParams(page, size, incidentId, newStatus, previousStatus, action,
                                                                                from, to, userId, principal));
    }
}
