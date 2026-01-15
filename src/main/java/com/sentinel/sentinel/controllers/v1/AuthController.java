package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.AuthenticationDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.services.AuthService;
import com.sentinel.sentinel.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                data.email(), data.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Users authenticatedUser = (Users) auth.getPrincipal();

        if(authenticatedUser != null) {
            String jwtToken = tokenService.signToken(authenticatedUser);
            return ResponseEntity.ok(new AuthenticatedUserDTO(
                    authenticatedUser.getId(), authenticatedUser.getName(), authenticatedUser.getEmail(),
                    authenticatedUser.getRole().name(), jwtToken
            ));
        }

        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticatedUserDTO> register(@RequestBody @Valid RegisterUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(data));
    }
}
