package com.gabreudev.sige.entities.calendar.dto;

import java.time.LocalDate;

public record UnavailableDateRequestDTO(
        LocalDate date,
        String reason
) {
}
