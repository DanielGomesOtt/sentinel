package com.sentinel.sentinel.infra.security;

import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.utils.ApiVersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private SecurityExceptionHandler securityExceptionHandler;

    @Value("${api.version}")
    private String API_VERSION;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        String version  = ApiVersionUtil.normalize(this.API_VERSION);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(securityExceptionHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(HttpMethod.POST, version + "/auth/register").permitAll();
                    request.requestMatchers(HttpMethod.POST, version + "/auth/login").permitAll();
                    request.requestMatchers(HttpMethod.POST, version + "/auth/token").permitAll();
                    request.requestMatchers(HttpMethod.POST, version + "/users").hasRole(Roles.ADMIN.name());
                    request.requestMatchers(HttpMethod.POST, version + "/systemIntegration").hasRole(Roles.ADMIN.name());
                    request.requestMatchers(HttpMethod.PUT, version + "/incidents").hasRole(Roles.TECH.name());
                    request.requestMatchers(HttpMethod.POST, version + "/incidents/system_integration").hasRole(Roles.SYSTEM.name());
                    request.requestMatchers("/swagger-ui/**").permitAll();
                    request.requestMatchers("/v3/api-docs/**").permitAll();
                    request.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_TECH
        ROLE_TECH > ROLE_USER
    """);
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
