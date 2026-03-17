package com.sentinel.sentinel.dto.incident;

import java.util.List;

public record PaginatedIncidentsDTO(
        List<CreatedIncidentDTO> incidents,
        boolean first,
        boolean last,
        int page,
        int numberOfElements,
        int size,
        long totalElements,
        int totalPages
) {
}
