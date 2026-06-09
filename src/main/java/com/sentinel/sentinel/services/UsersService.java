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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CreatedUserDTO create(CreateUserDTO data) {
        Optional<Users> alreadyExists = usersRepository.findByEmailAndStatus(data.email(), 1);

        if(alreadyExists.isPresent()) {
            throw new UserAlreadyExistException("This email is already in use.");
        }

        Optional<Organization> organization = organizationRepository.findByIdAndStatus(data.organizationId(), 1);

        if(organization.isPresent()) {
            String encodedPassword = passwordEncoder.encode(data.password());

            Users user = new Users(data.name(), data.email(), encodedPassword, organization.get(),
                    Roles.valueOf(data.role().toUpperCase()), 1);

            Users createdUser = usersRepository.save(user);

            return new CreatedUserDTO(createdUser);
        }

        throw new OrganizationNotFoundException("The informed organization was not found.");
    }

}
