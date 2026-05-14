package com.sentinel.sentinel.schedulers;

import com.sentinel.sentinel.services.IncidentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IncidentScheduler {

    private final IncidentService incidentService;

    public IncidentScheduler(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void verifyExpiredSla() {
        try {
            incidentService.verifyExpiredSla();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
