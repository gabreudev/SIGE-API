package com.gabreudev.sige.entities.user.dto;

public record AdminUpdateDTO(
        String username,
        String email,
        String password,
        Boolean enabled
) {}

