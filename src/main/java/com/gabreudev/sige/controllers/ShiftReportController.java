package com.gabreudev.sige.controllers;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.report.dto.*;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.services.ShiftReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("shift-reports")
@RequiredArgsConstructor
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class ShiftReportController {

    private final ShiftReportService shiftReportService;

    @PostMapping
    public ResponseEntity<ShiftReportResponseDTO> create(@RequestBody ShiftReportCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ShiftReportResponseDTO created = shiftReportService.create(dto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftReportResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody ShiftReportUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ShiftReportResponseDTO updated = shiftReportService.update(id, dto, currentUser.getId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ShiftReportResponseDTO> review(
            @PathVariable UUID id,
            @RequestBody ShiftReportReviewDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ShiftReportResponseDTO reviewed = shiftReportService.review(id, dto, currentUser.getId());
        return ResponseEntity.ok(reviewed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftReportResponseDTO> findById(@PathVariable UUID id) {
        ShiftReportResponseDTO report = shiftReportService.findById(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<ShiftReportResponseDTO> findByShiftId(@PathVariable UUID shiftId) {
        ShiftReportResponseDTO report = shiftReportService.findByShiftId(shiftId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShiftReportResponseDTO>> findByUserId(@PathVariable UUID userId) {
        List<ShiftReportResponseDTO> reports = shiftReportService.findByUserId(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShiftReportResponseDTO>> findByStatus(@PathVariable ReportStatus status) {
        List<ShiftReportResponseDTO> reports = shiftReportService.findByStatus(status);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/unity/{unityId}")
    public ResponseEntity<List<ShiftReportResponseDTO>> findByUnityId(@PathVariable UUID unityId) {
        List<ShiftReportResponseDTO> reports = shiftReportService.findByUnityId(unityId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping
    public ResponseEntity<List<ShiftReportResponseDTO>> findAll() {
        List<ShiftReportResponseDTO> reports = shiftReportService.findAll();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<ShiftReportResponseDTO>> findMyReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<ShiftReportResponseDTO> reports = shiftReportService.findByUserId(currentUser.getId());
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        shiftReportService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
