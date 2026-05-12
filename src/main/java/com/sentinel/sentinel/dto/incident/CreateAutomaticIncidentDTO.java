package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import jakarta.validation.constraints.NotNull;

public record CreateAutomaticIncidentDTO(
        CreateIncidentDTO incident,
        String message,
        IncidentLogLevel incidentLogLevel,
        String stacktrace
) {
}
