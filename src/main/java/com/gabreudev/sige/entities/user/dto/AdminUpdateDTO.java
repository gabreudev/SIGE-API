package com.gabreudev.sige.entities.user.dto;

public record AdminUpdateDTO(
        String username,
        String name,
        String email,
        String password,
        Boolean enabled
) {}

