package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.report.ReportStatus;
import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.shift.dto.ShiftCreateDTO;
import com.gabreudev.sige.entities.shift.dto.ShiftResponseDTO;
import com.gabreudev.sige.entities.shift.dto.ShiftUpdateDTO;
import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.repositories.ShiftRepository;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;

    private final UserRepository userRepository;

    private final UnityRepository unityRepository;

    @Transactional
    public ShiftResponseDTO create(ShiftCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Unity unity = dto.unityId() != null ? unityRepository.findById(dto.unityId())
                .orElseThrow(() -> new RuntimeException("Unity not found")) : null;

        Shift shift = new Shift();
        shift.setUser(user);
        shift.setUnity(unity);
        shift.setType(dto.type());
        shift.setHours(dto.hours());
        shift.setPeriod(dto.period());
        shift.setDate(dto.date());

        Shift saved = shiftRepository.save(shift);
        return new ShiftResponseDTO(saved);
    }

    public ShiftResponseDTO findById(UUID id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        return new ShiftResponseDTO(shift);
    }

    public List<ShiftResponseDTO> findAll() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findByUserId(UUID userId) {
        return shiftRepository.findByUserId(userId).stream()
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findByUnityId(UUID unityId) {
        return shiftRepository.findByUnity_Id(unityId).stream()
                .map(ShiftResponseDTO::new)
                .toList();
    }

    @Transactional
    public ShiftResponseDTO update(UUID id, ShiftUpdateDTO dto) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        if (dto.unityId() != null) {
            Unity unity = unityRepository.findById(dto.unityId())
                    .orElseThrow(() -> new RuntimeException("Unity not found"));
            shift.setUnity(unity);
        }
        if (dto.type() != null) shift.setType(dto.type());
        if (dto.hours() != null) shift.setHours(dto.hours());
        if (dto.period() != null) shift.setPeriod(dto.period());
        if (dto.date() != null) shift.setDate(dto.date());

        Shift updated = shiftRepository.save(shift);
        return new ShiftResponseDTO(updated);
    }


    @Transactional
    public void delete(UUID id) {
        if (!shiftRepository.existsById(id)) {
            throw new RuntimeException("Shift not found");
        }
        shiftRepository.deleteById(id);
    }

    // Métodos úteis para contabilização
    public List<ShiftResponseDTO> findWithReports() {
        return shiftRepository.findAll().stream()
                .filter(shift -> shift.getReport() != null)
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findWithApprovedReports() {
        return shiftRepository.findAll().stream()
                .filter(shift -> shift.getReport() != null && shift.getReport().getStatus() == ReportStatus.APPROVED)
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findWithPendingReports() {
        return shiftRepository.findAll().stream()
                .filter(shift -> shift.getReport() != null && shift.getReport().getStatus() == ReportStatus.SUBMITTED)
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findWithRejectedReports() {
        return shiftRepository.findAll().stream()
                .filter(shift -> shift.getReport() != null && shift.getReport().getStatus() == ReportStatus.REJECTED)
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findWithoutReports() {
        return shiftRepository.findAll().stream()
                .filter(shift -> shift.getReport() == null)
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public List<ShiftResponseDTO> findByUserIdWithReportStatus(UUID userId, ReportStatus status) {
        return shiftRepository.findByUserId(userId).stream()
                .filter(shift -> shift.getReport() != null && shift.getReport().getStatus() == status)
                .map(ShiftResponseDTO::new)
                .toList();
    }
}
