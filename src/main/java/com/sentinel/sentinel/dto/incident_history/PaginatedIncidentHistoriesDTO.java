package com.sentinel.sentinel.dto.incident_history;

import java.util.List;

public record PaginatedIncidentHistoriesDTO(
        List<CreatedIncidentHistory> incidentHistories,
        boolean first,
        boolean last,
        int page,
        int numberOfElements,
        int size,
        long totalElements,
        int totalPages
) {
}
