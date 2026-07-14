package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.auth.ForgotPasswordEmail;
import com.sentinel.sentinel.dto.auth.ResetUserPasswordDTO;
import com.sentinel.sentinel.dto.system_integration.ClientAuthDTO;
import com.sentinel.sentinel.dto.system_integration.TokenResponseDTO;
import com.sentinel.sentinel.exceptions.ResetCodeException;
import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.*;
import com.sentinel.sentinel.repositories.ForgotPasswordTokenRepository;
import com.sentinel.sentinel.repositories.OrganizationRepository;
import com.sentinel.sentinel.repositories.SystemIntegrationRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UsersRepository usersRepository;

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final SystemIntegrationRepository systemIntegrationRepository;

    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    private final EmailService emailService;

    public AuthService(SystemIntegrationRepository systemIntegrationRepository, TokenService tokenService,
                       PasswordEncoder passwordEncoder, OrganizationRepository organizationRepository,
                       UsersRepository usersRepository, ForgotPasswordTokenRepository forgotPasswordTokenRepository, ForgotPasswordTokenRepository forgotPasswordTokenRepository1, EmailService emailService) {
        this.systemIntegrationRepository = systemIntegrationRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.organizationRepository = organizationRepository;
        this.usersRepository = usersRepository;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository1;
        this.emailService = emailService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmailAndStatus(email, 1)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Could not complete the login with the provided information."
                ));

        return new AuthenticatedPrincipal(user.getId().toString(), user.getEmail(), user.getPasswordHash(), "user",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())), user.getOrganization().getId(), user, null);
    }

    public AuthenticatedUserDTO register (RegisterUserDTO user) {
        Optional<Users> alreadyExist = usersRepository.findByEmailAndStatus(user.email(), 1);

        if(alreadyExist.isPresent()) {
            throw new UserAlreadyExistException("Could not complete the registration with the provided information.");
        }

        Organization organization = new Organization(user.organizationName(), 1);
        Organization savedOrganization = organizationRepository.save(organization);

        String encodedPassword = passwordEncoder.encode(user.password());

        Users newUser = new Users(user.name(), user.email(), encodedPassword, savedOrganization, Roles.ADMIN, 1);
        Users savedUser = usersRepository.save(newUser);

        String token = tokenService.signUserToken(savedUser);

        return new AuthenticatedUserDTO(savedUser, token);
    }

    public TokenResponseDTO generateToken(ClientAuthDTO data) {
        Optional<SystemIntegration> clientOpt = systemIntegrationRepository.findByClientId(data.clientId());

        if (clientOpt.isEmpty()) {
            throw new UsernameNotFoundException(
                    "Could not complete the authentication with the provided information."
            );
        }

        SystemIntegration client = clientOpt.get();

        boolean valid = BCrypt.checkpw(data.clientSecret(), client.getClientSecretHash());

        if (!valid || !client.isActive()) {
            throw new UsernameNotFoundException(
                    "Could not complete the authentication with the provided information."
            );
        }

        String token = tokenService.signSystemToken(client);
        return new TokenResponseDTO(token);
    }

    @Transactional
    public void confirmUserByEmail(ForgotPasswordEmail data) {
        Optional<Users> user = usersRepository.findByEmailAndStatus(data.email(), 1);

        if(user.isPresent()) {
            String randomToken = createForgotPasswordToken();
            String hashedRandomToken = passwordEncoder.encode(randomToken);

            String message = "This is your reset code: " + randomToken + "\n";
            message += "This code is valid for 15 minutes.";

            emailService.sendEmail(user.get().getEmail(), "Password Reset code", message);

            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken(user.get(), hashedRandomToken,
                    Instant.now().plus(Duration.ofMinutes(15)), false, Instant.now());
            forgotPasswordTokenRepository.save(forgotPasswordToken);
        }

    }

    @Transactional
    public void resetUserPassword(ResetUserPasswordDTO data) {
        Optional<Users> user = usersRepository.findByEmailAndStatus(data.email(), 1);

        if (user.isPresent()) {
            Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordTokenRepository
                    .findByUserAndUsed(user.get(), false);

            if (forgotPasswordToken.isPresent()) {
                if (passwordEncoder.matches(data.code(), forgotPasswordToken.get().getCode())
                        && !forgotPasswordToken.get().getExpiresAt().isBefore(Instant.now())) {

                    forgotPasswordToken.get().setUsed(true);
                    forgotPasswordTokenRepository.save(forgotPasswordToken.get());

                    String newPasswordHashed = passwordEncoder.encode(data.newPassword());
                    user.get().setPasswordHash(newPasswordHashed);
                    usersRepository.save(user.get());
                }

                throw new ResetCodeException("The provided password reset code is invalid or has expired.");

            }

            throw new ResetCodeException("The provided password reset code is invalid or has expired.");
        }

        throw new ResetCodeException("The provided password reset code is invalid or has expired.");
    }

    private String createForgotPasswordToken() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
