package com.gabreudev.sige.entities.shift.dto;

import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.shift.ShiftPeriod;
import com.gabreudev.sige.entities.shift.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShiftResponseDTO(
        UUID id,
        UUID userId,
        String userName,
        String userRegistration,
        UUID unityId,
        String unityName,
        ShiftType type,
        Integer hours,
        ShiftPeriod period,
        LocalDate date,
        Boolean validated,
        LocalDateTime validationDate,
        UUID validatedByUserId,
        String validatedByUserName
) {
    public ShiftResponseDTO(Shift shift) {
        this(
                shift.getId(),
                shift.getUser().getId(),
                shift.getUser().getName(),
                shift.getUser().getRegistration(),
                shift.getUnity().getId(),
                shift.getUnity().getName(),
                shift.getType(),
                shift.getHours(),
                shift.getPeriod(),
                shift.getDate(),
                shift.getValidated(),
                shift.getValidationDate(),
                shift.getValidatedBy() != null ? shift.getValidatedBy().getId() : null,
                shift.getValidatedBy() != null ? shift.getValidatedBy().getName() : null
        );
    }
}
