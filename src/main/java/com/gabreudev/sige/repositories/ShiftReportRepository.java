package com.gabreudev.sige.repositories;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.report.ShiftReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftReportRepository extends JpaRepository<ShiftReport, UUID> {
    Optional<ShiftReport> findByShiftId(UUID shiftId);
    List<ShiftReport> findByShift_UserId(UUID userId);
    List<ShiftReport> findByStatus(ReportStatus status);
    List<ShiftReport> findByShift_Unity_Id(UUID unityId);
}
