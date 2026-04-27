package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.incident.CreateIncidentDTO;
import com.sentinel.sentinel.dto.incident.CreatedIncidentDTO;
import com.sentinel.sentinel.dto.incident.PaginatedIncidentsDTO;
import com.sentinel.sentinel.dto.incident.UpdateIncidentDTO;
import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.exceptions.IncidentAlreadyClosedException;
import com.sentinel.sentinel.exceptions.IncidentNotFoundException;
import com.sentinel.sentinel.exceptions.UserNotAuthenticatedException;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.IncidentHistory;
import com.sentinel.sentinel.models.SlaRule;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.*;
import com.sentinel.sentinel.specifications.IncidentSpecification;
import com.sentinel.sentinel.utils.AuthenticatedPrincipalUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    @Autowired
    private SlaRuleRepository slaRuleRepository;


    @Transactional
    public CreatedIncidentDTO createIncident(CreateIncidentDTO data, Users user) {


        if(user != null) {
            SlaRule sla = slaRuleRepository.findById(data.severity().name()).get();
            Instant slaInstant = Instant.now().plus(Duration.ofHours(sla.getDurationHours()));
            Incident incident = new Incident(data.title(), data.description(), data.severity(),
                    IncidentStatus.OPEN, data.serviceName(), slaInstant, false, user,
                    user.getOrganization(), Instant.now(), Instant.now());

            Incident createdIncident = incidentRepository.save(incident);
            IncidentHistory createdIncidentHistory = new IncidentHistory(
                    createdIncident, null, "OPEN", "create incident",
                    user, incident.getCreatedAt());

            incidentHistoryRepository.save(createdIncidentHistory);

            return new CreatedIncidentDTO(createdIncident);
        }

        throw new UserNotAuthenticatedException("The user is not authenticated.");
    }

    public CreatedIncidentDTO getIncidentById(Long incidentId, Users user) {


        Optional<Incident> incident;
        if(user != null) {
            if (user.getRole().name().equals("USER")) {
                incident = incidentRepository.findByIdAndCreatedByOrganizationAndCreatedBy(incidentId,
                        user.getOrganization(), user);
            } else {
                incident = incidentRepository.findByIdAndCreatedByOrganization(incidentId,
                        user.getOrganization());
            }

            if(incident.isPresent()) {
                return new CreatedIncidentDTO(incident.get());
            }

            throw new IncidentNotFoundException("The specified incident was not found.");
        }

        throw new UserNotAuthenticatedException("The user is not authenticated.");
    }

    public PaginatedIncidentsDTO findAll(int page, int size, String title, String description, String severity,
                                         String status, String serviceName, String slaDeadline, Boolean slaViolate,
                                         Users user) {

        Long userId = null;
        Long organizationId = user.getOrganization().getId();

        if(user.getRole().name().equals("USER")) {
            userId = user.getId();
        }

        Pageable pagination = PageRequest.of(page, size);
        Severity severityEnum = null;
        IncidentStatus statusEnum = null;
        Instant slaDeadlineInstant = null;
        if(severity != null) {
            severityEnum = Severity.valueOf(severity.toUpperCase());
        }

        if(status != null) {
            statusEnum = IncidentStatus.valueOf(status.toUpperCase());
        }

        if(slaDeadline != null) {
             slaDeadlineInstant = LocalDateTime
                    .parse(slaDeadline, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .toInstant(ZoneOffset.UTC);
        }
        Specification<Incident> spec = Specification
                .where(IncidentSpecification.organizationId(organizationId))
                .and(IncidentSpecification.userId(userId))
                .and(IncidentSpecification.title(title))
                .and(IncidentSpecification.description(description))
                .and(IncidentSpecification.severity(severityEnum))
                .and(IncidentSpecification.status(statusEnum))
                .and(IncidentSpecification.serviceName(serviceName))
                .and(IncidentSpecification.slaDeadline(slaDeadlineInstant))
                .and(IncidentSpecification.slaViolated(slaViolate));

        Page<Incident> incidents = incidentRepository.findAll(spec, pagination);
        List<CreatedIncidentDTO> formattedIncidents = incidents.getContent().stream()
                                                                            .map(CreatedIncidentDTO::new).toList();

        return new PaginatedIncidentsDTO(formattedIncidents, incidents.isFirst(), incidents.isLast(),
                incidents.getNumber(), incidents.getNumberOfElements(), incidents.getSize(),
                incidents.getTotalElements(), incidents.getTotalPages());
    }

    @Transactional
    public UpdateIncidentDTO updateIncident(@Valid UpdateIncidentDTO data, Users user) {

        if(user.getRole() == Roles.TECH && data.incidentStatus() == IncidentStatus.CLOSED){
            data = new UpdateIncidentDTO(data.incidentId(), data.title(), data.description(), data.severity(),
                    data.serviceName());
        }

        Optional<Incident> incident = incidentRepository.findByIdAndCreatedByOrganization(data.incidentId(),
                                                                        user.getOrganization());

        String previousStatus = null;

        if(incident.isPresent()) {

            previousStatus = incident.get().getIncidentStatus().name();


            if(incident.get().getIncidentStatus() == IncidentStatus.CLOSED){
                throw new IncidentAlreadyClosedException("Incident is already closed and cannot be updated.");
            }

            if(data.title() != null && !data.title().isEmpty()) {
                incident.get().setTitle(data.title());
            }

            if(data.description() != null && !data.description().isEmpty()) {
                incident.get().setDescription(data.description());
            }

            if(data.serviceName() != null && !data.serviceName().isEmpty()) {
                incident.get().setServiceName(data.serviceName());
            }

            if(data.severity() != null) {
                incident.get().setSeverity(data.severity());
            }

            if(data.incidentStatus() != null) {
                System.out.println(data.toString());
                incident.get().setIncidentStatus(data.incidentStatus());
            }

            incident.get().setUpdatedAt(Instant.now());

            Incident updatedIncident = incidentRepository.save(incident.get());
            IncidentHistory updatedIncidentHistory = new IncidentHistory(
                    updatedIncident, previousStatus, updatedIncident.getIncidentStatus().name(),
                    "update incident",
                    user, incident.get().getUpdatedAt());

            incidentHistoryRepository.save(updatedIncidentHistory);

            return new UpdateIncidentDTO(
                    updatedIncident.getId(),
                    updatedIncident.getTitle(),
                    updatedIncident.getDescription(),
                    updatedIncident.getSeverity(),
                    updatedIncident.getServiceName(),
                    updatedIncident.getIncidentStatus()
            );
        }

        throw new IncidentNotFoundException("The specified incident was not found.");
    }
}
