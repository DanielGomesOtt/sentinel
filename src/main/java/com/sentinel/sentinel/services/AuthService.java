package com.sentinel.sentinel.services;

import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
import com.sentinel.sentinel.dto.auth.AuthenticatedUserDTO;
import com.sentinel.sentinel.dto.auth.RegisterUserDTO;
import com.sentinel.sentinel.enums.Roles;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import com.sentinel.sentinel.repositories.OrganizationRepository;
import com.sentinel.sentinel.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersRepository.findByEmailAndStatus(email, 1)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Could not complete the login with the provided information."
                ));
    }

    public AuthenticatedUserDTO register (RegisterUserDTO user) {
        Optional<UserDetails> alreadyExist = usersRepository.findByEmailAndStatus(user.email(), 1);

        if(alreadyExist.isPresent()) {
            throw new UserAlreadyExistException("Could not complete the registration with the provided information.");
        }

        Organization organization = new Organization(user.organizationName(), 1);
        Organization savedOrganization = organizationRepository.save(organization);

        String encodedPassword = passwordEncoder.encode(user.password());

        Users newUser = new Users(user.name(), user.email(), encodedPassword, savedOrganization, Roles.ADMIN, 1);
        Users savedUser = usersRepository.save(newUser);

        String token = tokenService.signToken(savedUser);

        return new AuthenticatedUserDTO(savedUser, token);
    }
}
