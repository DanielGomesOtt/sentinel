package com.sentinel.sentinel.dto.incident_log;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.IncidentLog;

public record CreatedIncidentLogDTO(
       Long incidentId,
       IncidentLogLevel level,
       String message,
       String stackTrace,
       String serviceName
) {
    public CreatedIncidentLogDTO(IncidentLog incidentLog) {
        this(incidentLog.getIncidentId().getId(),  incidentLog.getLevel(), incidentLog.getMessage(),
                incidentLog.getStackTrace(), incidentLog.getServiceName());
    }
}
