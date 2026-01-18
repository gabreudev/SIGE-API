package com.gabreudev.sige.entities.unity.dto;


import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.unity.UnityRole;

import java.util.Map;
import java.util.UUID;

public record UnityResponseDTO(
        UUID id,
        String name,
        String address,
        UnityRole unityRole,
        UUID preceptorId,
        String preceptorName,
        Map<String, Object> availability
) {
    public UnityResponseDTO(Unity unity) {
        this(
                unity.getId(),
                unity.getName(),
                unity.getAddress(),
                unity.getUnityRole(),
                unity.getPreceptor() != null ? unity.getPreceptor().getId() : null,
                unity.getPreceptor() != null ? unity.getPreceptor().getName() : null,
                unity.getAvailability()
        );
    }
}

