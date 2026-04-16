package com.sentinel.sentinel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sla_rule")
public class SlaRule {

    @Id
    private String severity;
    @Column(name = "duration_hours")
    private int durationHours;

    public String getSeverity() {
        return severity;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours( int hours) {
        this.durationHours = hours;
    }
}
