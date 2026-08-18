package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.enums.IncidentLogLevel;

public record CreateAutomaticIncidentDTO(
        CreateIncidentDTO incident,
        String message,
        IncidentLogLevel incidentLogLevel,
        String stacktrace
) {
}
