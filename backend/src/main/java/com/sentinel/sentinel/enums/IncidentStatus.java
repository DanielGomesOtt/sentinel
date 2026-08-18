package com.sentinel.sentinel.enums;

import java.util.ArrayList;
import java.util.List;

public enum IncidentStatus {
    OPEN("open"),
    UNDER_REVIEW("under_review"),
    IN_CORRECTION("in correction"),
    RESOLVED("resolved"),
    CLOSED("closed");

    private String incidentStatus;

    IncidentStatus(String incidentStatus) {
        this.incidentStatus = incidentStatus;
    }

    public String getIncidentStatus() {
        return incidentStatus;
    }

    public boolean validateIncidentStatusSort(IncidentStatus previousStatus, IncidentStatus newStatus) {
        List<IncidentStatus> listStatus = new ArrayList<>();
        listStatus.add(OPEN);
        listStatus.add(UNDER_REVIEW);
        listStatus.add(IN_CORRECTION);
        listStatus.add(RESOLVED);
        listStatus.add(CLOSED);

        int indexPreviousStatus = listStatus.indexOf(previousStatus);
        int indexNewStatus = listStatus.indexOf(newStatus);

        return (indexNewStatus - 1 == indexPreviousStatus && indexNewStatus > 0) ||
                (indexNewStatus + 1 == indexPreviousStatus) && indexNewStatus < listStatus.size() - 1;

    }
}
