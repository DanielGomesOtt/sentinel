package com.sentinel.sentinel.services;

import com.sentinel.sentinel.dto.users.CreateUserDTO;
import com.sentinel.sentinel.dto.users.CreatedUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.exceptions.OrganizationNotFoundException;
import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private UsersService usersService;

    Users standardUser;

    Users techUser;

    Organization organization;

    @BeforeEach
    void setup() {
        this.organization = new Organization(1L, "organization", 1);
        this.standardUser = new Users(1L, "user", "user@email.com",
                "encoded password", organization, Roles.USER, 1);
        this.techUser = new Users(2L, "user2", "user2@email.com",
                "encoded password", organization, Roles.TECH, 1);
    }

    @Test
    @DisplayName("create should create a new user")
    void createShouldCreateNewUser() {
        CreateUserDTO data = new CreateUserDTO("user", "user@email.com", "normal password",
                1L, "USER");

        when(usersRepository.findByEmailAndStatus(standardUser.getEmail(), 1)).thenReturn(Optional.empty());
        when(organizationRepository.findByIdAndStatus(data.organizationId(), 1))
                .thenReturn(Optional.of(organization));
        when(passwordEncoder.encode(data.password())).thenReturn(standardUser.getPassword());
        when(usersRepository.save(new Users("user", "user@email.com",
                "encoded password", organization, Roles.USER, 1)))
                .thenReturn(standardUser);

        CreatedUserDTO createdUser = usersService.create(data);

        assertNotNull(createdUser);
        assertEquals(standardUser.getId(), createdUser.id());
        assertEquals(standardUser.getName(), createdUser.name());
        assertEquals(standardUser.getEmail(), createdUser.email());
        assertNotEquals(data.password(), standardUser.getPassword());
    }

    @Test
    @DisplayName("create should throw UserAlreadyExistException")
    void createShouldThrowUserAlreadyExistException() {
        CreateUserDTO data = new CreateUserDTO("user", "user@email.com", "normal password",
                1L, "USER");

        when(usersRepository.findByEmailAndStatus(data.email(), 1)).thenReturn(Optional.of(standardUser));

        assertThrows(UserAlreadyExistException.class, () -> {
            usersService.create(data);
        });
    }

    @Test
    @DisplayName("create should throw OrganizationNotFoundException")
    void createShouldThrowOrganizationNotFoundException() {
        CreateUserDTO data = new CreateUserDTO("user", "user@email.com", "normal password",
                1L, "USER");

        when(usersRepository.findByEmailAndStatus(data.email(), 1)).thenReturn(Optional.empty());
        when(organizationRepository.findByIdAndStatus(data.organizationId(), 1))
                .thenReturn(Optional.empty());

        assertThrows(OrganizationNotFoundException.class, () -> {
            usersService.create(data);
        });
    }
}