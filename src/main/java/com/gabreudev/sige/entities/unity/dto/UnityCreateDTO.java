package com.gabreudev.sige.entities.unity.dto;

import com.gabreudev.sige.entities.unity.UnityRole;

import java.util.Map;
import java.util.UUID;

public record UnityCreateDTO(
        String name,
        String address,
        UnityRole unityRole,
        UUID preceptorId,
        Map<String, Object> availability
) {
}
