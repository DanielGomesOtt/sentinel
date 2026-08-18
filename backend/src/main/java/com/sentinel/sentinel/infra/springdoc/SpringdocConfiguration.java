package com.sentinel.sentinel.infra.springdoc;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SpringdocConfiguration {

    @Bean
    public GroupedOpenApi v1API() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public OpenAPI v1OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentinel")
                        .description("Sentinel is a backend portfolio project built with Java 25 and Spring Boot. " +
                                "It showcases a production-like backend architecture with JWT authentication, role-based access control, incident lifecycle management, system integration, audit logging, history tracking, PDF reporting, and SLA monitoring. " +
                                "The API highlights backend design with Spring Security, Spring Data JPA and PostgreSQL, Flyway migrations, validation, exception handling, and OpenAPI documentation. " +
                                "Use the documented endpoints to explore authentication flows, user and integration management, incident workflows, search filters, paginated results, and operational reporting in a portfolio-grade backend sample.\n\n" +
                                "GitHub repository: [https://github.com/DanielGomesOtt/sentinel](https://github.com/DanielGomesOtt/sentinel)")
                        .version("1.0"));
    }
}
