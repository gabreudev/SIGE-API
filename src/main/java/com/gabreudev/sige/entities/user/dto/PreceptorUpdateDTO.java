package com.gabreudev.sige.entities.user.dto;

public record PreceptorUpdateDTO(
        String username,
        String name,
        String email,
        String registration,
        Boolean enabled,
        Boolean male
) {}

