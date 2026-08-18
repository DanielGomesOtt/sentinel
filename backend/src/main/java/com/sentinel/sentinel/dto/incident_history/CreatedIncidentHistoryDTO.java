package com.sentinel.sentinel.dto.incident_history;

import com.sentinel.sentinel.models.IncidentHistory;

public record CreatedIncidentHistoryDTO(
        Long id,
        Long incidentId,
        String previousStatus,
        String newStatus,
        String action,
        Long performedBy,
        Long performedBySystemIntegration,
        String createdAt
) {

    public CreatedIncidentHistoryDTO(IncidentHistory incidentHistory) {
        this(incidentHistory.getId(), incidentHistory.getIncidentId().getId(), incidentHistory.getPreviousStatus(),
                incidentHistory.getNewStatus(), incidentHistory.getAction(),
                (incidentHistory.getPerformedBy() != null ? incidentHistory.getPerformedBy().getId() : null),
                (incidentHistory.getPerformedBySystemIntegration() != null ?
                        incidentHistory.getPerformedBySystemIntegration().getId() : null),
                incidentHistory.getCreatedAt().toString());
    }
}
