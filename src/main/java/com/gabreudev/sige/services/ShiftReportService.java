package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.report.ShiftReport;
import com.gabreudev.sige.entities.report.dto.*;
import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.repositories.ShiftReportRepository;
import com.gabreudev.sige.repositories.ShiftRepository;
import com.gabreudev.sige.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftReportService {

    private final ShiftReportRepository shiftReportRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShiftReportResponseDTO create(ShiftReportCreateDTO dto, UUID studentId) {
        Shift shift = shiftRepository.findById(dto.shiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify that the shift belongs to the student
        if (!shift.getUser().getId().equals(studentId)) {
            throw new RuntimeException("You can only create reports for your own shifts");
        }

        // Check if report already exists for this shift
        if (shiftReportRepository.findByShiftId(dto.shiftId()).isPresent()) {
            throw new RuntimeException("Report already exists for this shift");
        }

        ShiftReport report = new ShiftReport();
        report.setShift(shift);
        report.setUser(student);
        report.setContent(dto.content());
        report.setStatus(ReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());

        ShiftReport saved = shiftReportRepository.save(report);
        return new ShiftReportResponseDTO(saved);
    }

    @Transactional
    public ShiftReportResponseDTO update(UUID reportId, ShiftReportUpdateDTO dto, UUID userId) {
        ShiftReport report = shiftReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Verify that the report belongs to the user
        if (!report.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own reports");
        }

        // Don't allow editing if already approved
        if (report.getStatus() == ReportStatus.APPROVED) {
            throw new RuntimeException("Cannot edit an approved report");
        }

        // Allow editing only SUBMITTED or REJECTED reports
        if (report.getStatus() != ReportStatus.SUBMITTED && report.getStatus() != ReportStatus.REJECTED) {
            throw new RuntimeException("Can only edit submitted or rejected reports");
        }

        // Aluno pode editar apenas o conteúdo, não o status
        if (dto.content() != null) {
            report.setContent(dto.content());
        }

        ShiftReport updated = shiftReportRepository.save(report);
        return new ShiftReportResponseDTO(updated);
    }

    @Transactional
    public ShiftReportResponseDTO review(UUID reportId, ShiftReportReviewDTO dto, UUID reviewerId) {
        ShiftReport report = shiftReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        // Check if user has permission to review
        if (!isAuthorizedToReview(reviewer)) {
            throw new RuntimeException("Only ADMIN, SUPERVISOR, and PRECEPTOR can review reports");
        }

        // Allow review of submitted and rejected reports
        if (report.getStatus() != ReportStatus.SUBMITTED && report.getStatus() != ReportStatus.REJECTED) {
            throw new RuntimeException("Only submitted or rejected reports can be reviewed");
        }

        report.setStatus(dto.approved() ? ReportStatus.APPROVED : ReportStatus.REJECTED);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewedBy(reviewer);

        ShiftReport updated = shiftReportRepository.save(report);
        return new ShiftReportResponseDTO(updated);
    }

    public ShiftReportResponseDTO findById(UUID id) {
        ShiftReport report = shiftReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return new ShiftReportResponseDTO(report);
    }

    public ShiftReportResponseDTO findByShiftId(UUID shiftId) {
        ShiftReport report = shiftReportRepository.findByShiftId(shiftId)
                .orElseThrow(() -> new RuntimeException("Report not found for this shift"));
        return new ShiftReportResponseDTO(report);
    }

    public List<ShiftReportResponseDTO> findByUserId(UUID userId) {
        return shiftReportRepository.findByUserId(userId).stream()
                .map(ShiftReportResponseDTO::new)
                .toList();
    }

    public List<ShiftReportResponseDTO> findByStatus(ReportStatus status) {
        return shiftReportRepository.findByStatus(status).stream()
                .map(ShiftReportResponseDTO::new)
                .toList();
    }

    public List<ShiftReportResponseDTO> findByUnityId(UUID unityId) {
        return shiftReportRepository.findByShift_Unity_Id(unityId).stream()
                .map(ShiftReportResponseDTO::new)
                .toList();
    }

    public List<ShiftReportResponseDTO> findAll() {
        return shiftReportRepository.findAll().stream()
                .map(ShiftReportResponseDTO::new)
                .toList();
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        ShiftReport report = shiftReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Verify that the report belongs to the user or user is admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!report.getUser().getId().equals(userId) && user.getUserRole() != UserRole.ADMIN) {
            throw new RuntimeException("You can only delete your own reports");
        }

        // Don't allow deleting if already approved
        if (report.getStatus() == ReportStatus.APPROVED) {
            throw new RuntimeException("Cannot delete an approved report");
        }

        shiftReportRepository.deleteById(id);
    }

    private boolean isAuthorizedToReview(User user) {
        return user.getUserRole() == UserRole.ADMIN
                || user.getUserRole() == UserRole.SUPERVISOR
                || user.getUserRole() == UserRole.PRECEPTOR;
    }
}
