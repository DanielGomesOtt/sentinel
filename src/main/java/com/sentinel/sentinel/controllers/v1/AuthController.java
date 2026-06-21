package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.AuthenticationDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.dto.system_integration.ClientAuthDTO;
import com.sentinel.sentinel.dto.system_integration.TokenResponseDTO;
import com.sentinel.sentinel.exceptions.UserNotFoundException;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.UsersRepository;
import com.sentinel.sentinel.services.AuthService;
import com.sentinel.sentinel.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Authentication", description = "Here are the requests used to perform login and registration.")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final AuthService authService;

    private final UsersRepository usersRepository;


    public AuthController(UsersRepository usersRepository, AuthService authService, TokenService tokenService, AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.authService = authService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    @Operation(summary = "Login endpoint", description = "Authenticates a user and returns an access token.")
    public ResponseEntity<AuthenticatedUserDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                data.email(), data.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        AuthenticatedPrincipal authenticatedUser = (AuthenticatedPrincipal) auth.getPrincipal();

        Users user = usersRepository.findById(Long.valueOf(authenticatedUser.getId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        String jwtToken = tokenService.signUserToken(user);
        return ResponseEntity.ok(new AuthenticatedUserDTO(
                user.getId(), user.getName(), user.getEmail(),
                user.getRole().name(), jwtToken
        ));
    }

    @PostMapping("/token")
    @Operation(summary = "Generate token for system integration")
    public ResponseEntity<TokenResponseDTO> generateToken(@RequestBody @Valid ClientAuthDTO data) {
        return ResponseEntity.ok(authService.generateToken(data));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register root user",
            description = "Creates the initial root user for the organization. This endpoint can only be used when no users exist yet."
    )
    public ResponseEntity<AuthenticatedUserDTO> register(@RequestBody @Valid RegisterUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(data));
    }
}
