package com.sentinel.sentinel.infra.security;

import com.auth0.jwt.interfaces.Claim;
import com.sentinel.sentinel.repositories.UsersRepository;
import com.sentinel.sentinel.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = this.recoverToken(request);

        if(authorization != null) {
            Map<String, Claim> claims = tokenService.verifyToken(authorization);
            Optional<UserDetails> user = usersRepository.findByEmailAndStatus(claims.get("email").toString(), 1 );

            if(user.isPresent()) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        user.get(), null, user.get().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if(authorization != null) {
            return authorization.replace("Bearer ", "");
        }

        return null;
    }
}
