package com.gabreudev.sige.entities.user;

public record UserRegisterDTO(
        String username,
        String name,
        String email,
        String password,
        String registration,
        UserRole userRole,
        InternshipRole internshipRole,
        Boolean male
) {}
