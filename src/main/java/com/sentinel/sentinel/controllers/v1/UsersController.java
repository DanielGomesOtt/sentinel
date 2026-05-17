package com.sentinel.sentinel.controllers.v1;

import com.sentinel.sentinel.dto.users.CreateUserDTO;
import com.sentinel.sentinel.dto.users.CreatedUserDTO;
import com.sentinel.sentinel.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "Here are the requests used to perform the functionality related to users.")
public class UsersController {

    @Autowired
    private UsersService usersService;


    @PostMapping
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user. The user can have the role of admin, technician, or standard user."
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatedUserDTO> create(@RequestBody @Valid CreateUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.create(data));
    }
}
