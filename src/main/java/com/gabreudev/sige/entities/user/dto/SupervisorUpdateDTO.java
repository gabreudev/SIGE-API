package com.gabreudev.sige.entities.user.dto;

public record SupervisorUpdateDTO(
        String username,
        String email,
        String coren,
        Boolean enabled
) {}

