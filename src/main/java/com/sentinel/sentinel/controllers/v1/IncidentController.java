package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.incident.*;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.services.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/incidents")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Incidents", description = "Here are the requests used to perform the functionality related to incidents.")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @PostMapping
    @Operation(
            summary = "Create an incident manually.",
            description = "Creates a new incident manually."
    )
    public ResponseEntity<CreatedIncidentDTO> createManually(@RequestBody @Valid CreateIncidentDTO data,
                                                             @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        CreatedIncidentDTO createdIncident = incidentService.createIncident(data, principal);

        URI uri = URI.create("/v1/incidents/" + createdIncident.id());

        return ResponseEntity
                .created(uri)
                .body(createdIncident);
    }

    @PostMapping("/system_integration")
    @Operation(
            summary = "Create an incident by system integration.",
            description = "Creates a new incident by system integration."
    )
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<CreatedIncidentBySystemIntegrationDTO> createIncidentBySystemIntegration(@RequestBody @Valid CreateAutomaticIncidentDTO data,
                                                            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        CreatedIncidentBySystemIntegrationDTO createdIncident = incidentService.createIncidentBySystemIntegration(data, principal);

        URI uri = URI.create("/v1/incidents/system_integration" + createdIncident.id());

        return ResponseEntity
                .created(uri)
                .body(createdIncident);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find an incident by id.",
            description = "Retrieves a specific incident by its unique identifier. "
                    + "The user must be authenticated and can only access incidents "
                    + "belonging to their organization."
    )
    public ResponseEntity<CreatedIncidentDTO> findIncidentById(@PathVariable Long id,
                                                               @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return ResponseEntity.ok().body(incidentService.getIncidentById(id, principal));
    }

    @GetMapping
    @Operation(
            summary = "Find incidents by params.",
            description = "Retrieves incidents by params. "
                    + "The user must be authenticated and can only access incidents "
                    + "belonging to their organization."
    )
    public ResponseEntity<PaginatedIncidentsDTO> findAll(@RequestParam(required = true) int page,
                                                         @RequestParam(required = true) int size,
                                                         @RequestParam(required = false) String title,
                                                         @RequestParam(required = false) String description,
                                                         @RequestParam(required = false) String severity,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String serviceName,
                                                         @RequestParam(required = false) String slaDeadline,
                                                         @RequestParam(required = false) boolean slaViolate,
                                                         @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return ResponseEntity.ok(incidentService.findAll(page, size, title, description, severity, status, serviceName, slaDeadline,
                                            slaViolate, principal));
    }

    @PutMapping
    @Operation(summary = "Update an incident",
            description = "Updates an existing incident by its ID. Allows modification of fields such as title, " +
                    "description, severity, status, and related service information."
    )
    @PreAuthorize("hasRole('TECH')")
    public ResponseEntity<UpdateIncidentDTO> updateManually(@RequestBody @Valid UpdateIncidentDTO data,
                                                            @AuthenticationPrincipal AuthenticatedPrincipal principal){
        return ResponseEntity.ok(incidentService.updateIncident(data, principal));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam(required = true) int page,
                                              @RequestParam(required = true) int size,
                                              @RequestParam(required = false) String title,
                                              @RequestParam(required = false) String description,
                                              @RequestParam(required = false) String severity,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) String serviceName,
                                              @RequestParam(required = false) String slaDeadline,
                                              @RequestParam(required = false) boolean slaViolate,
                                              @AuthenticationPrincipal AuthenticatedPrincipal principal) throws Exception {
        byte[] pdf = incidentService.generateIncidentsPdf(page, size, title, description, severity, status, serviceName, slaDeadline,
                slaViolate, principal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=incidents.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
