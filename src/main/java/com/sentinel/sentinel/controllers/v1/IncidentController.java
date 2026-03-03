package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.services.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/v1/incidents")
@Tag(name = "Incidents", description = "Here are the requests used to perform the functionality related to incidents.")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @PostMapping
    @Operation(
            summary = "Create an incident manually.",
            description = "Creates a new incident manually."
    )
    public ResponseEntity<CreatedIncidentDTO> createManually(@RequestBody @Valid CreateIncidentDTO data) {
        CreatedIncidentDTO createdIncident = incidentService.createIncident(data);

        URI uri = URI.create("/v1/incidents/" + createdIncident.id());

        return ResponseEntity
                .created(uri)
                .body(createdIncident);
    }
}
