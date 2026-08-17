package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.incident.*;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.services.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @Operation(
            summary = "Create an incident manually",
            description = "Creates a new incident using the provided incident details. The authenticated user context is used to associate the incident with the correct organization."
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
            summary = "Create an incident via system integration",
            description = "Accepts incident data from an external integration and creates a new incident record. Requires a system integration role and authenticates the caller before creating the incident."
    )
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<CreatedIncidentBySystemIntegrationDTO> createIncidentBySystemIntegration(@RequestBody @Valid CreateAutomaticIncidentDTO data,
                                                            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        CreatedIncidentBySystemIntegrationDTO createdIncident = incidentService.createIncidentBySystemIntegration(data, principal);

        URI uri = URI.create("/v1/incidents/system_integration/" + createdIncident.id());

        return ResponseEntity
                .created(uri)
                .body(createdIncident);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find an incident by ID",
            description = "Retrieves a single incident using its unique identifier. The authenticated user must belong to the same organization as the incident owner, otherwise access is denied."
    )
    public ResponseEntity<CreatedIncidentDTO> findIncidentById(@PathVariable Long id,
                                                               @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return ResponseEntity.ok(incidentService.getIncidentById(id, principal));
    }

    @GetMapping
    @Operation(
            summary = "Search incidents with filters",
            description = "Returns paginated incidents for the authenticated user's organization. Optional filters such as title, description, severity, status, service name, SLA deadline, and SLA violation can be applied to narrow results."
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
    @Operation(
            summary = "Update an existing incident",
            description = "Updates the fields of an existing incident, such as title, description, severity, status, and service information. This operation requires the authenticated user to have the TECH role."
    )
    @PreAuthorize("hasRole('TECH')")
    public ResponseEntity<UpdateIncidentDTO> updateManually(@RequestBody @Valid UpdateIncidentDTO data,
                                                            @AuthenticationPrincipal AuthenticatedPrincipal principal){
        return ResponseEntity.ok(incidentService.updateIncident(data, principal));
    }

    @GetMapping("/pdf")
    @Operation(
            summary = "Generate incidents report in PDF",
            description = "Generates a PDF report containing the incidents that match the provided filter criteria. " +
                    "The report supports page/size pagination and optional filters, and returns the result as a downloadable PDF file."
    )
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
