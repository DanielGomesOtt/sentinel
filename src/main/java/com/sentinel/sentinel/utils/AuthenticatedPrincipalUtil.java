package com.sentinel.sentinel.utils;

import com.sentinel.sentinel.exceptions.UserNotAuthenticatedException;
import com.sentinel.sentinel.models.Users;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedPrincipalUtil {

    private AuthenticatedPrincipalUtil() {
    }

    public static Users getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            throw new UserNotAuthenticatedException("User not authenticated");
        }

        return (Users) authentication.getPrincipal();
    }
}
