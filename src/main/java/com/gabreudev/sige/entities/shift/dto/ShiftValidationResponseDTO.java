package com.gabreudev.sige.entities.shift.dto;

import java.util.List;

public record ShiftValidationResponseDTO(
        String message,
        Integer validatedCount,
        List<ShiftResponseDTO> validatedShifts
) {
}
