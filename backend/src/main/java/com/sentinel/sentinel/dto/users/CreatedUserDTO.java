package com.sentinel.sentinel.dto.users;


import com.sentinel.sentinel.models.Users;

public record CreatedUserDTO(
     Long id,
     String name,
     String email,
     Long organizationId,
     String Role) {


    public CreatedUserDTO(Users createdUser) {
        this(createdUser.getId(), createdUser.getName(), createdUser.getEmail(), createdUser.getOrganization().getId(),
                createdUser.getRole().toString());
    }
}
