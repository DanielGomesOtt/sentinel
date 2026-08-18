package com.sentinel.sentinel.schedulers;

import com.sentinel.sentinel.services.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IncidentScheduler {

    private static final Logger log = LoggerFactory.getLogger(IncidentScheduler.class);
    private final IncidentService incidentService;

    public IncidentScheduler(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void verifyExpiredSla() {
        try {
            incidentService.verifyExpiredSla();
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
