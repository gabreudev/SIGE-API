package com.gabreudev.sige.entities.calendar.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.gabreudev.sige.entities.calendar.UnavailableDate;

public record UnavailableDateResponseDTO(
        UUID id,
        LocalDate date,
        String reason
) {
    public static UnavailableDateResponseDTO fromEntity(UnavailableDate entity) {
        return new UnavailableDateResponseDTO(
                entity.getId(),
                entity.getDate(),
                entity.getReason()
        );
    }
}
