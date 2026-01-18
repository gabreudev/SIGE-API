package com.gabreudev.sige.entities.user.dto;

public record SupervisorUpdateDTO(
        String username,
        String name,
        String email,
        String registration,
        Boolean enabled,
        Boolean male
) {}

