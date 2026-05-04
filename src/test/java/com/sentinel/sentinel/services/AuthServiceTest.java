package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
import com.sentinel.sentinel.infra.security.SecurityConfiguration;
import com.sentinel.sentinel.models.AuthenticatedPrincipal;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.OrganizationRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private SecurityConfiguration securityConfiguration;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Users user;


    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup () {
        this.user = new Users(
                1L,
                "Daniel",
                "daniel@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.ADMIN,
                1
        );
    }

    @Test
    @DisplayName("load user by username should return an user details instance")
    void loadUserByUsernameShouldReturnUserDetails() {

        when(usersRepository.findByEmailAndStatus(user.getEmail(), 1)).thenReturn(Optional.of(user));

        UserDetails loadUser = authService.loadUserByUsername(user.getEmail());

        assertNotNull(loadUser);
        assertEquals(user.getEmail(), loadUser.getUsername());
    }

    @Test
    @DisplayName("load user by username should throw an UsernameNotFoundException")
    void loadUserByUsernameShouldThrowUsernameNotFoundException() {

        when(usersRepository.findByEmailAndStatus(user.getEmail(), 1))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(user.getEmail());
        });
    }

    @Test
    @DisplayName("register should save an user with ADMIN role")
    void registerShouldSaveUserWithAdminRole() {

        when(usersRepository.findByEmailAndStatus("daniel@email.com", 1))
                .thenReturn(Optional.empty());

        when(organizationRepository.save(any(Organization.class)))
                .thenReturn(user.getOrganization());

        when(passwordEncoder.encode("normal password"))
                .thenReturn("encoded password");

        when(usersRepository.save(any(Users.class)))
                .thenReturn(user);

        when(tokenService.signUserToken(user))
                .thenReturn("token");

        RegisterUserDTO dto = new RegisterUserDTO(
                "Daniel",
                "daniel@email.com",
                "normal password",
                "organization"
        );


        AuthenticatedUserDTO result = authService.register(dto);


        assertNotNull(result);
        assertEquals("daniel@email.com", result.email());
        assertEquals("token", result.token());
        assertEquals(Roles.ADMIN.name(), result.role());
    }

    @Test
    @DisplayName("register should throw UserAlreadyExistException")
    void registerShouldThrowUserAlreadyExistException() {

        when(usersRepository.findByEmailAndStatus("daniel@email.com", 1))
                .thenReturn(Optional.of(user));

        RegisterUserDTO dto = new RegisterUserDTO(
                "Daniel",
                "daniel@email.com",
                "normal password",
                "organization"
        );

        assertThrows(UserAlreadyExistException.class, () -> {
            authService.register(dto);
        });
    }

}