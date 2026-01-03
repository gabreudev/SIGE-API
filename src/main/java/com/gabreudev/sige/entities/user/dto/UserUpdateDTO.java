package com.gabreudev.sige.entities.user.dto;

import com.gabreudev.sige.entities.user.InternshipRole;

public record UserUpdateDTO(
        String username,
        String email,
        String coren, //only supervisor
        String registration, // only student
        InternshipRole internshipRole, // only student
        Boolean enabled
) {}
