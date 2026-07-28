package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.auth.*;
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
import java.util.Map;


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
    @Operation(
            summary = "Authenticate user and issue JWT token",
            description = "Validates user credentials, authenticates the user, and returns an access token along with user profile details. " +
                    "The returned JWT can be used to access protected endpoints in the API."
    )
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
    @Operation(
            summary = "Issue JWT for system integration clients",
            description = "Validates the credentials of an external system client and returns a JWT token specifically intended for system integration requests. " +
                    "Use this token to authenticate calls to protected integration endpoints."
    )
    public ResponseEntity<TokenResponseDTO> generateToken(@RequestBody @Valid ClientAuthDTO data) {
        return ResponseEntity.ok(authService.generateToken(data));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register the initial root user",
            description = "Creates the first root-level user for the organization. This endpoint is intended for initial system setup and can only be used when there are no existing users."
    )
    public ResponseEntity<AuthenticatedUserDTO> register(@RequestBody @Valid RegisterUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(data));
    }

    @PostMapping("/forgot_password/reset_code")
    @Operation(
            summary = "Request a password reset code",
            description = "Generates a password reset verification code and attempts to send it to the provided email address. " +
                    "The response does not expose whether the email exists to preserve security best practices."
    )
    public ResponseEntity<Map<String, String>> generatePasswordResetCode(
            @RequestBody @Valid ForgotPasswordEmail data) {

        authService.confirmUserByEmail(data);

        return ResponseEntity.ok(
                Map.of("message", "If the email is registered, a verification code has been sent.")
        );
    }

    @PostMapping("/forgot_password/reset_password")
    @Operation(
            summary = "Reset user password using verification code",
            description = "Accepts a valid password reset code and a new password, verifies the code, and updates the user's password. " +
                    "Returns a confirmation message when the operation completes successfully."
    )
    public ResponseEntity<Map<String, String>> resetUserPassword(@RequestBody @Valid ResetUserPasswordDTO data) {

        authService.resetUserPassword(data);

        return ResponseEntity.ok(Map.of("message", "The password has been successfully reset."));
    }
}
