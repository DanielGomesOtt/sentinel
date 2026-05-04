package com.sentinel.sentinel.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.AuthenticationDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.UsersRepository;
import com.sentinel.sentinel.services.AuthService;
import com.sentinel.sentinel.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UsersRepository usersRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("login should return 200 and token when credentials are valid")
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {

        AuthenticationDTO request = new AuthenticationDTO(
                "user@email.com",
                "123456"
        );

        Users user = new Users(
                1L,
                "User",
                "user@email.com",
                "encodedPassword",
                null,
                Roles.USER,
                1
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(tokenService.signUserToken(user))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    @DisplayName("register should return 201 created")
    void registerShouldReturn201Created() throws Exception {

        Users user = new Users(
                1L,
                "user",
                "user@email.com",
                "encodedPassword",
                null,
                Roles.USER,
                1
        );

        RegisterUserDTO request = new RegisterUserDTO("user", "user@email.com", "password",
                "organization");

        AuthenticatedUserDTO authenticatedUserDTO = new AuthenticatedUserDTO(user, "token");

        when(authService.register(request)).thenReturn(authenticatedUserDTO);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("token"));
    }
}