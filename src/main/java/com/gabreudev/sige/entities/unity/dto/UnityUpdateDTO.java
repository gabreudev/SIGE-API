package com.gabreudev.sige.entities.unity.dto;

import java.util.Map;
import java.util.UUID;

public record UnityUpdateDTO(
        String name,
        String address,
        UUID preceptorId,
        Map<String, Object> availability
) {
}

