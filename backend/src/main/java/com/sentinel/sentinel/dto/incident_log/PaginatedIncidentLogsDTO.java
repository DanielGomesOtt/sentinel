package com.sentinel.sentinel.dto.incident_log;

import java.util.List;

public record PaginatedIncidentLogsDTO(
        List<CreatedIncidentLogDTO> incidentLogs,
        boolean first,
        boolean last,
        int page,
        int numberOfElements,
        int size,
        long totalElements,
        int totalPages
) {
}
