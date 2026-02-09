package com.gabreudev.sige.entities.shift.dto;

import java.util.List;
import java.util.UUID;

public record ShiftValidationDTO(
        List<UUID> shiftIds
) {
}
