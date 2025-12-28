package com.gabreudev.sige.entities.user;

public record UserRegisterDTO(
        String username,
        String email,
        String password,
        String coren,
        String registration,
        UserRole userRole,
        InternshipRole internshipRole
) {}
