package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident_history.CreatedIncidentHistory;
import com.sentinel.sentinel.dto.incident_history.PaginatedIncidentHistoriesDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.models.IncidentHistory;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.IncidentHistoryRepository;
import com.sentinel.sentinel.specifications.IncidentHistorySpecification;
import com.sentinel.sentinel.utils.AuthenticatedPrincipalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncidentHistoryService {

    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    public PaginatedIncidentHistoriesDTO findHistoriesByParams(int page, int size, Long incidentId, String newStatus,
                                                       String previousStatus, String action, String from, String to,
                                                       Long userId, Users user) {
        Long organizationId = user.getOrganization().getId();

        if(user.getRole().name().equals("USER")) {
            userId = user.getId();
        }

        Pageable pagination = PageRequest.of(page, size);
        IncidentStatus newStatusEnum = null;
        IncidentStatus previousStatusEnum = null;
        Instant fromInstant = null;
        Instant toInstant = null;

        if(newStatus != null) {
            newStatusEnum = IncidentStatus.valueOf(newStatus);
        }

        if(previousStatus != null) {
            previousStatusEnum = IncidentStatus.valueOf(previousStatus);
        }

        if(from != null) {
            fromInstant = LocalDateTime
                    .parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .toInstant(ZoneOffset.UTC);
        }

        if(to != null) {
            toInstant = LocalDateTime
                    .parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .toInstant(ZoneOffset.UTC);
        }

        Specification<IncidentHistory> spec = Specification
                .where(IncidentHistorySpecification.userId(userId))
                .and(IncidentHistorySpecification.incidentId(incidentId))
                .and(IncidentHistorySpecification.newStatus(newStatusEnum))
                .and(IncidentHistorySpecification.previousStatus(previousStatusEnum))
                .and(IncidentHistorySpecification.action(action))
                .and(IncidentHistorySpecification.from(fromInstant))
                .and(IncidentHistorySpecification.to(toInstant));

        Page<IncidentHistory> incidentHistories = incidentHistoryRepository.findAll(spec, pagination);

        List<CreatedIncidentHistory> formattedIncidentHistories = incidentHistories.getContent().stream()
                .map(CreatedIncidentHistory::new).toList();

        return new PaginatedIncidentHistoriesDTO(formattedIncidentHistories, incidentHistories.isFirst(), incidentHistories.isLast(),
                incidentHistories.getNumber(), incidentHistories.getNumberOfElements(), incidentHistories.getSize(),
                incidentHistories.getTotalElements(), incidentHistories.getTotalPages());
    }
}
