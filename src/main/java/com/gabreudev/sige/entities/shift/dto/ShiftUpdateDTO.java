package com.gabreudev.sige.entities.shift.dto;

import com.gabreudev.sige.entities.shift.ShiftPeriod;
import com.gabreudev.sige.entities.shift.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShiftUpdateDTO(
        UUID unityId,
        ShiftType type,
        Integer hours,
        ShiftPeriod period,
        LocalDate date,
        Boolean validated,
        LocalDateTime validationDate,
        UUID validatedByUserId
) {}
