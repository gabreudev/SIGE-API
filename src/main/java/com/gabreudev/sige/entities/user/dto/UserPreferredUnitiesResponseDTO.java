package com.gabreudev.sige.entities.user.dto;

import com.gabreudev.sige.entities.unity.Unity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserPreferredUnitiesResponseDTO(
        UUID userId,
        String userName,
        List<PreferredUnityInfo> preferredUnities
) {
    public record PreferredUnityInfo(
            UUID unityId,
            String unityName,
            String address
    ) {
    }

    public static UserPreferredUnitiesResponseDTO fromUser(com.gabreudev.sige.entities.user.User user, List<Unity> unities) {
        List<PreferredUnityInfo> unityInfos = unities.stream()
                .map(unity -> new PreferredUnityInfo(
                        unity.getId(),
                        unity.getName(),
                        unity.getAddress()
                ))
                .collect(Collectors.toList());

        return new UserPreferredUnitiesResponseDTO(
                user.getId(),
                user.getName(),
                unityInfos
        );
    }
}

