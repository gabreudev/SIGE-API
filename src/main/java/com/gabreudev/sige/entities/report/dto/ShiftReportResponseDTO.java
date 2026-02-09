package com.gabreudev.sige.entities.report.dto;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.report.ShiftReport;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShiftReportResponseDTO(
        UUID id,
        UUID userId,
        String userName,
        String userRegistration,
        UUID shiftId,
        String shiftDate,
        String shiftType,
        Integer hours,
        String content,
        ReportStatus status,
        LocalDateTime createdAt,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        UUID reviewedByUserId,
        String reviewedByUserName
) {
    public ShiftReportResponseDTO(ShiftReport report) {
        this(
                report.getId(),
                report.getUser().getId(),
                report.getUser().getName(),
                report.getUser().getRegistration(),
                report.getShift().getId(),
                report.getShift().getDate().toString(),
                report.getShift().getType().name(),
                report.getShift().getHours(),
                report.getContent(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getSubmittedAt(),
                report.getReviewedAt(),
                report.getReviewedBy() != null ? report.getReviewedBy().getId() : null,
                report.getReviewedBy() != null ? report.getReviewedBy().getName() : null
        );
    }
}
