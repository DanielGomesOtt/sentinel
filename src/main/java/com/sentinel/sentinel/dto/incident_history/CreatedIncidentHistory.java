package com.sentinel.sentinel.dto.incident_history;

import com.sentinel.sentinel.models.IncidentHistory;

public record CreatedIncidentHistory(
        Long id,
        Long incidentId,
        String previousStatus,
        String newStatus,
        String action,
        Long performedBy,
        String createdAt
) {

    public CreatedIncidentHistory(IncidentHistory incidentHistory) {
        this(incidentHistory.getId(), incidentHistory.getIncidentId().getId(), incidentHistory.getPreviousStatus(),
                incidentHistory.getNewStatus(), incidentHistory.getAction(),  incidentHistory.getPerformedBy().getId(),
                incidentHistory.getCreatedAt().toString());
    }
}
