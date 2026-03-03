package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.exceptions.UserNotFoundException;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.IncidentHistory;
import com.sentinel.sentinel.models.SlaRule;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    @Autowired
    private IncidentLogRepository incidentLogRepository;

    @Autowired
    private SlaRuleRepository slaRuleRepository;

    @Autowired
    private UsersRepository usersRepository;

    public CreatedIncidentDTO createIncident(CreateIncidentDTO data) {
        Optional<Users> user = usersRepository.findByIdAndStatus(data.userId(), 1);

        if(user.isPresent()) {
            SlaRule sla = slaRuleRepository.findById(data.severity().name()).get();
            Instant slaInstant = Instant.now().plus(Duration.ofHours(sla.getDurationHours()));
            Incident incident = new Incident(data.title(), data.description(), data.severity(),
                    IncidentStatus.OPEN, data.serviceName(), slaInstant, false, user.get(),
                    Instant.now(), Instant.now());

            Incident createdIncident = incidentRepository.save(incident);
            IncidentHistory createdIncidentHistory = new IncidentHistory(
                    createdIncident, null, "OPEN", "create incident",
                    user.get(), incident.getCreatedAt());

            incidentHistoryRepository.save(createdIncidentHistory);

            return new CreatedIncidentDTO(createdIncident);
        }

        throw new UserNotFoundException("The specified user was not found.");
    }
}
