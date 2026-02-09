package com.gabreudev.sige.entities.report.dto;

import java.util.UUID;

public record ShiftReportCreateDTO(
        UUID shiftId,
        String content
) {
}
