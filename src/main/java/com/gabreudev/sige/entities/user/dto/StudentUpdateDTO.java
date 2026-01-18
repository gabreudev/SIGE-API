package com.gabreudev.sige.entities.user.dto;

import com.gabreudev.sige.entities.user.InternshipRole;

public record StudentUpdateDTO(
        String username,
        String name,
        String email,
        String registration,
        InternshipRole internshipRole,
        Boolean enabled,
        Boolean male
) {}
