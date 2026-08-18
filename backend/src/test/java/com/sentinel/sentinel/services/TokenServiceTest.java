package com.sentinel.sentinel.services;

import com.auth0.jwt.interfaces.Claim;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private Users user;

    private TokenService tokenService;

    @BeforeEach
    void setup() throws Exception {
        tokenService = new TokenService();

        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, "test-secret-123");

        Field durationField = TokenService.class.getDeclaredField("duration");
        durationField.setAccessible(true);
        durationField.set(tokenService, 24L);

        user = new Users(
                1L,
                "User Test",
                "user@test.com",
                "encoded-password",
                new Organization(1L, "Org Test", 1),
                Roles.ADMIN,
                1
        );
    }

    @Test
    @DisplayName("sign token should return jwt token")
    void signTokenShouldReturnJwtToken() {

        String result = tokenService.signUserToken(user);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("verifyToken should return claims when token is valid")
    void verifyTokenShouldReturnClaims() {

        String token = tokenService.signUserToken(user);

        Map<String, Claim> claims = tokenService.verifyToken(token);

        assertNotNull(claims);
        assertEquals("user@test.com", claims.get("email").asString());
        assertEquals("1", claims.get("id").asString());
        assertEquals("ADMIN", claims.get("role").asString());
        assertEquals("sentinel", claims.get("iss").asString());
    }
}
