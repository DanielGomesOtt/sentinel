package com.sentinel.sentinel.infra.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.SystemIntegration;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.SystemIntegrationRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import com.sentinel.sentinel.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SystemIntegrationRepository systemIntegrationRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            try {
                Map<String, Claim> claims = tokenService.verifyToken(token);
                String type = claims.get("type").asString();

                if ("user".equals(type)) {
                    authenticateUser(claims);
                }

                if ("system".equals(type)) {
                    authenticateSystem(claims);
                }

            } catch (Exception exception) {
                throw new JWTVerificationException(exception.getMessage(), exception);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(Map<String, Claim> claims) {

        String email = claims.get("email").asString();

        Optional<Users> userOpt = usersRepository.findByEmailAndStatus(email, 1);

        if (userOpt.isEmpty()) return;

        Users user = userOpt.get();

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                user.getId().toString(),
                user.getEmail(),
                null,
                claims.get("type").asString(),
                authorities,
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user,
                null
        );

        setAuthentication(principal);
    }

    private void authenticateSystem(Map<String, Claim> claims) {

        String clientId = claims.get("client_id").asString();

        Optional<SystemIntegration> clientOpt =
                systemIntegrationRepository.findByClientId(clientId);

        if (clientOpt.isEmpty() || !clientOpt.get().isActive()) return;

        SystemIntegration client = clientOpt.get();

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_SYSTEM")
        );

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                client.getClientId(),
                client.getClientId(),
                null,
                claims.get("type").asString(),
                authorities,
                null,
                null,
                client
        );

        setAuthentication(principal);
    }

    private void setAuthentication(AuthenticatedPrincipal principal) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recoverToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.replace("Bearer ", "");
        }

        return null;
    }
}
