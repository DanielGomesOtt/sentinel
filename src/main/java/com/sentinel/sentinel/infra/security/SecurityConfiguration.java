package com.sentinel.sentinel.infra.security;

import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.utils.ApiVersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Value("${api.version}")
    private String API_VERSION;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        String version  = ApiVersionUtil.normalize(this.API_VERSION);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(HttpMethod.POST, version + "/auth/register").permitAll();
                    request.requestMatchers(HttpMethod.POST, version + "/auth/login").permitAll();
                    request.requestMatchers(HttpMethod.POST, version + "/users").hasRole(Roles.ADMIN.name());
                    request.requestMatchers("/api/"+version+"/auth/login").permitAll();
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
}
