package com.gabreudev.sige.entities.user.dto;

import java.util.List;
import java.util.UUID;

public record UserPreferredUnitiesDTO(
        List<UUID> unityIds
) {
}
