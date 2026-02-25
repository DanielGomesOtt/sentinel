package com.sentinel.sentinel.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIncidentDTO(
        @NotNull(message = "User ID is required to create this incident.")
        Long id,
        @NotBlank(message = "Incident title must be provided.")
        String title,
        @NotBlank(message = "Incident description must be provided.")
        String description,
        @NotBlank(message = "Incident severity must be provided.")
        String severity,
        @NotBlank(message = "Incident service must be provided.")
        String serviceName
) {
}
