package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
import com.sentinel.sentinel.repositories.IncidentLogRepository;
import com.sentinel.sentinel.repositories.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    @Autowired
    private IncidentLogRepository incidentLogRepository;

    public CreatedIncidentDTO createIncident(CreateIncidentDTO data) {
        return null;
    }
}
