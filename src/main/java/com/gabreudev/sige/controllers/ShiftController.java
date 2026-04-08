package com.gabreudev.sige.controllers;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.shift.dto.*;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.services.InternshipAllocationService;
import com.gabreudev.sige.services.ShiftService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("shifts")
@RequiredArgsConstructor
@SecurityRequirement(name = SecurityConfigurations.SECURITY)

public class ShiftController {

    private final ShiftService shiftService;
    private final InternshipAllocationService internshipAllocationService;

    @PostMapping
    public ResponseEntity<ShiftResponseDTO> create(@RequestBody ShiftCreateDTO dto) {
        ShiftResponseDTO created = shiftService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🔥 ROTA ESPECÍFICA - DEVE VIR ANTES DE /{id}
    @GetMapping("/with-validation")
    public ResponseEntity<List<ShiftWithReportDTO>> getShiftsWithValidation(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID unityId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ShiftWithReportDTO> shifts = shiftService.findShiftsWithValidation(userId, unityId, startDate, endDate);
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> findById(@PathVariable UUID id) {
        ShiftResponseDTO shift = shiftService.findById(id);
        return ResponseEntity.ok(shift);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponseDTO>> findAll() {
        List<ShiftResponseDTO> shifts = shiftService.findAll();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShiftResponseDTO>> findByUserId(@PathVariable UUID userId) {
        List<ShiftResponseDTO> shifts = shiftService.findByUserId(userId);
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/unity/{unityId}")
    public ResponseEntity<List<ShiftResponseDTO>> findByUnityId(@PathVariable UUID unityId) {
        List<ShiftResponseDTO> shifts = shiftService.findByUnityId(unityId);
        return ResponseEntity.ok(shifts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> update(@PathVariable UUID id, @RequestBody ShiftUpdateDTO dto) {
        ShiftResponseDTO updated = shiftService.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        shiftService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-reports")
    public ResponseEntity<List<ShiftResponseDTO>> findWithReports() {
        List<ShiftResponseDTO> shifts = shiftService.findWithReports();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/reports/approved")
    public ResponseEntity<List<ShiftResponseDTO>> findWithApprovedReports() {
        List<ShiftResponseDTO> shifts = shiftService.findWithApprovedReports();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<List<ShiftResponseDTO>> findWithPendingReports() {
        List<ShiftResponseDTO> shifts = shiftService.findWithPendingReports();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/reports/rejected")
    public ResponseEntity<List<ShiftResponseDTO>> findWithRejectedReports() {
        List<ShiftResponseDTO> shifts = shiftService.findWithRejectedReports();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/reports/none")
    public ResponseEntity<List<ShiftResponseDTO>> findWithoutReports() {
        List<ShiftResponseDTO> shifts = shiftService.findWithoutReports();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/user/{userId}/reports/{status}")
    public ResponseEntity<List<ShiftResponseDTO>> findByUserIdWithReportStatus(
            @PathVariable UUID userId,
            @PathVariable ReportStatus status) {
        List<ShiftResponseDTO> shifts = shiftService.findByUserIdWithReportStatus(userId, status);
        return ResponseEntity.ok(shifts);
    }

    @PostMapping("/allocate-students-internship1")
    public ResponseEntity<InternshipAllocationResponseDTO> allocateInternship1(@RequestBody InternshipAllocationRequestDTO request) {
        log.info("Received internship allocation request: {}", request);
        InternshipAllocationResponseDTO response = internshipAllocationService.allocateInternship1(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
