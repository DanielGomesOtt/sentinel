package com.sentinel.sentinel.enums;

public enum IncidentLogLevel {
    INFO("info"),
    WARN("warn"),
    ERROR("error");

    private String incidentLogLevel;

    IncidentLogLevel(String incidentLogLevel) {
        this.incidentLogLevel = incidentLogLevel;
    }

    public String getIncidentLogLevel() {
        return incidentLogLevel;
    }
}
