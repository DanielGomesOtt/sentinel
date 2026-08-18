package com.sentinel.sentinel.dto.incident_history;

import java.util.List;

public record PaginatedIncidentHistoriesDTO(
        List<CreatedIncidentHistoryDTO> incidentHistories,
        boolean first,
        boolean last,
        int page,
        int numberOfElements,
        int size,
        long totalElements,
        int totalPages
) {
}
