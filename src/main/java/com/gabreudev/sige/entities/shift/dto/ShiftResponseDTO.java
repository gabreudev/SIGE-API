package com.gabreudev.sige.entities.shift.dto;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.shift.ShiftPeriod;
import com.gabreudev.sige.entities.shift.ShiftType;

import java.time.LocalDate;
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
        UUID reportId,
        ReportStatus reportStatus,
        Boolean hasReport
) {
    public ShiftResponseDTO(Shift shift) {
        this(
                shift.getId(),
                shift.getUser().getId(),
                shift.getUser().getName(),
                shift.getUser().getRegistration(),
                shift.getUnity() != null ? shift.getUnity().getId() : null,
                shift.getUnity() != null ? shift.getUnity().getName() : null,
                shift.getType(),
                shift.getHours(),
                shift.getPeriod(),
                shift.getDate(),
                shift.getReport() != null ? shift.getReport().getId() : null,
                shift.getReport() != null ? shift.getReport().getStatus() : null,
                shift.getReport() != null
        );
    }
}
