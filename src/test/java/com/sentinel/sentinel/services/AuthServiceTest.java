package com.sentinel.sentinel.services;

import com.sentinel.sentinel.enums.Roles;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private TokenService tokenService;

    private Users user;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup () {
        this.user = new Users(
                1L,
                "Daniel",
                "daniel@email.com",
                "hashed-password",
                new Organization(),
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

}