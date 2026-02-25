package com.sentinel.sentinel.enums;

public enum Severity {

    CRITICAL("critical"),
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");

    private String severity;

    Severity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }
}
