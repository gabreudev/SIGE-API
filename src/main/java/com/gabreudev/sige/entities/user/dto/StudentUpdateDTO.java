package com.gabreudev.sige.entities.user.dto;

import com.gabreudev.sige.entities.user.InternshipRole;

public record StudentUpdateDTO(

        Boolean enabled,
        InternshipRole internshipRole,
        String registration,
        String email,
        String username
        ) {}
