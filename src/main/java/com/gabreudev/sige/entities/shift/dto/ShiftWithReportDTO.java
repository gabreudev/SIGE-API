package com.gabreudev.sige.entities.shift.dto;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.shift.ShiftPeriod;
import com.gabreudev.sige.entities.shift.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShiftWithReportDTO(
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
        ReportDetails report
) {
    public ShiftWithReportDTO(Shift shift) {
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
                shift.getReport() != null ? new ReportDetails(
                        shift.getReport().getId(),
                        shift.getReport().getContent(),
                        shift.getReport().getStatus(),
                        shift.getReport().getCreatedAt(),
                        shift.getReport().getSubmittedAt(),
                        shift.getReport().getReviewedAt(),
                        shift.getReport().getReviewedBy() != null ? shift.getReport().getReviewedBy().getId() : null,
                        shift.getReport().getReviewedBy() != null ? shift.getReport().getReviewedBy().getName() : null
                ) : null
        );
    }

    public record ReportDetails(
            UUID id,
            String content,
            ReportStatus status,
            LocalDateTime createdAt,
            LocalDateTime submittedAt,
            LocalDateTime reviewedAt,
            UUID reviewedByUserId,
            String reviewedByUserName
    ) {}
}
