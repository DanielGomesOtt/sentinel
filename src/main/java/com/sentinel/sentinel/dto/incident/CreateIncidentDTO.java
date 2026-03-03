package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIncidentDTO(
        @NotNull(message = "User ID is required to create this incident.")
        Long userId,
        @NotBlank(message = "Incident title must be provided.")
        String title,
        @NotBlank(message = "Incident description must be provided.")
        String description,
        @NotNull(message = "Incident severity must be provided. Send only valid severities.")
        Severity severity,
        @NotBlank(message = "Incident service must be provided.")
        String serviceName
) {
}
