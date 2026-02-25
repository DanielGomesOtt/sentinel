package com.sentinel.sentinel.enums;

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
}
