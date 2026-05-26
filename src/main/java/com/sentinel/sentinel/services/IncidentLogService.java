package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident_log.CreatedIncidentLogDTO;
import com.sentinel.sentinel.dto.incident_log.PaginatedIncidentLogsDTO;
import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.IncidentLog;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.IncidentLogRepository;
import com.sentinel.sentinel.specifications.IncidentLogSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IncidentLogService {

    private final IncidentLogRepository incidentLogRepository;

    public IncidentLogService(IncidentLogRepository incidentLogRepository) {
        this.incidentLogRepository = incidentLogRepository;
    }

    public PaginatedIncidentLogsDTO findLogsByParams(int page, int size, Long incidentId, IncidentLogLevel
                                                      incidentLogLevel, String message, String serviceName,
                                                      Instant from, Instant to, Long userId,
                                                      AuthenticatedPrincipal principal) {
        Users user = principal.getUser();

        Long organizationId = user.getOrganization().getId();

        if(user.getRole().name().equals("USER")) {
            userId = user.getId();
        }

        Pageable pagination = PageRequest.of(page, size);

        Specification<IncidentLog> spec = Specification
                .where(IncidentLogSpecification.userId(userId))
                .and(IncidentLogSpecification.incidentId(incidentId))
                .and(IncidentLogSpecification.incidentLogLevel(incidentLogLevel))
                .and(IncidentLogSpecification.OrganizationId(organizationId))
                .and(IncidentLogSpecification.message(message))
                .and(IncidentLogSpecification.serviceName(serviceName))
                .and(IncidentLogSpecification.from(from))
                .and(IncidentLogSpecification.to(to));

        Page<IncidentLog> incidentLogs = incidentLogRepository.findAll(spec, pagination);

        List<CreatedIncidentLogDTO> formattedIncidentLogs = incidentLogs.getContent().stream()
                .map(CreatedIncidentLogDTO::new).toList();

        return new PaginatedIncidentLogsDTO(formattedIncidentLogs, incidentLogs.isFirst(), incidentLogs.isLast(),
                incidentLogs.getNumber(), incidentLogs.getNumberOfElements(), incidentLogs.getSize(),
                incidentLogs.getTotalElements(), incidentLogs.getTotalPages());
    }

}
