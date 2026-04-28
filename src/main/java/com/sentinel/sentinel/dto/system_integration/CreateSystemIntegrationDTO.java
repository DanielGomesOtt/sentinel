package com.sentinel.sentinel.dto.system_integration;

import jakarta.validation.constraints.NotBlank;

public record CreateSystemIntegrationDTO(
        @NotBlank(message = "A system name is required to create an integration.")
        String name
) {
}
