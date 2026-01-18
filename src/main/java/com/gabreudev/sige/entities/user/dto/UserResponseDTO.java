package com.gabreudev.sige.entities.user.dto;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.InternshipRole;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRole;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponseDTO(
        UUID id,
        String name,
        String username,
        String email,
        String registration,
        UserRole userRole,
        InternshipRole internshipRole,
        Boolean enabled,
        Boolean male,
        List<PreferredUnityInfo> preferredUnities
) {
    public record PreferredUnityInfo(
            UUID unityId,
            String unityName,
            String address
    ) {
    }

    public static UserResponseDTO fromUser(User user, List<Unity> preferredUnities) {
        List<PreferredUnityInfo> unityInfos = preferredUnities.stream()
                .map(unity -> new PreferredUnityInfo(
                        unity.getId(),
                        unity.getName(),
                        unity.getAddress()
                ))
                .collect(Collectors.toList());

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRegistration(),
                user.getUserRole(),
                user.getInternshipRole(),
                user.getEnabled(),
                user.getMale(),
                unityInfos
        );
    }
}

