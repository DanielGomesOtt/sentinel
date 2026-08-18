package com.sentinel.sentinel.dto.incident_history;

import com.sentinel.sentinel.models.IncidentHistory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record IncidentHistoryPdfDTO(Long id,
                                    Long incidentId,
                                    String previous_status,
                                    String new_status,
                                    String action,
                                    String user,
                                    String integration,
                                    String createdAt) {

    public IncidentHistoryPdfDTO(IncidentHistory incidentHistory) {
        this(incidentHistory.getId(), incidentHistory.getIncidentId().getId(), incidentHistory.getPreviousStatus(),
                incidentHistory.getNewStatus(), incidentHistory.getAction(),
                (incidentHistory.getPerformedBy() != null ? incidentHistory.getPerformedBy().getName() : null),
                (incidentHistory.getPerformedBySystemIntegration() != null ?
                        incidentHistory.getPerformedBySystemIntegration().getName() : null),
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault())
                        .format(incidentHistory.getCreatedAt()));
    }

}
