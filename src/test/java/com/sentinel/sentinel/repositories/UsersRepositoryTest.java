package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Users user;

    private Organization organization;

    @BeforeEach
    void setup () {
        this.user = new Users(
                "Daniel",
                "daniel@email.com",
                "encoded password",
                new Organization("organization", 1),
                Roles.ADMIN,
                1
        );

        this.organization = new Organization("organization", 1);
    }

    @Test
    @DisplayName("find by email should return a user")
    void findByEmailShouldReturnUser() {
        Organization savedOrganization = organizationRepository.save(organization);
        user.setOrganization(savedOrganization);
        usersRepository.save(user);

        Optional<UserDetails> result = usersRepository.findByEmailAndStatus("daniel@email.com", 1);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("find by email should return a empty optional")
    void findByEmailShouldReturnEmptyOptional() {
        Optional<UserDetails> result = usersRepository.findByEmailAndStatus("daniel@email.com", 1);

        assertTrue(result.isEmpty());
    }
}