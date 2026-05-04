package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.system_integration.ClientAuthDTO;
import com.sentinel.sentinel.dto.system_integration.TokenResponseDTO;
import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.SystemIntegration;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.OrganizationRepository;
import com.sentinel.sentinel.repositories.SystemIntegrationRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SystemIntegrationRepository systemIntegrationRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmailAndStatus(email, 1)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Could not complete the login with the provided information."
                ));

        return new AuthenticatedPrincipal(user.getId().toString(), user.getEmail(), user.getPasswordHash(), "user",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())), user.getOrganization().getId());
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
}
