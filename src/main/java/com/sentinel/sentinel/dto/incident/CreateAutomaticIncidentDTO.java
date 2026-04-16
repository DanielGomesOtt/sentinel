package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import jakarta.validation.constraints.NotNull;

public record CreateAutomaticIncidentDTO(
        @NotNull
        CreateIncidentDTO incident,
        String message,
        IncidentLogLevel incidentLogLevel,
        String stacktrace
) {
}
