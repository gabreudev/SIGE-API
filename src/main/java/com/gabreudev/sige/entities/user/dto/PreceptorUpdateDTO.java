package com.gabreudev.sige.entities.user.dto;

public record PreceptorUpdateDTO(
        String username,
        String registration,
        String email,
        Boolean enabled
) {}

