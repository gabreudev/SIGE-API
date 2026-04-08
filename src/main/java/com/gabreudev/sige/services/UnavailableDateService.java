package com.gabreudev.sige.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gabreudev.sige.entities.calendar.UnavailableDate;
import com.gabreudev.sige.entities.calendar.dto.UnavailableDateRequestDTO;
import com.gabreudev.sige.entities.calendar.dto.UnavailableDateResponseDTO;
import com.gabreudev.sige.repositories.UnavailableDateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnavailableDateService {

    private final UnavailableDateRepository unavailableDateRepository;

    public UnavailableDateResponseDTO create(UnavailableDateRequestDTO dto) {
        validateDate(dto.date());

        if (unavailableDateRepository.existsByDate(dto.date())) {
            throw new RuntimeException("Já existe um dia indisponível cadastrado para esta data");
        }

        UnavailableDate created = unavailableDateRepository.save(new UnavailableDate(dto.date(), dto.reason()));
        return UnavailableDateResponseDTO.fromEntity(created);
    }

    public List<UnavailableDateResponseDTO> findAll() {
        return unavailableDateRepository.findAll().stream()
                .map(UnavailableDateResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UnavailableDateResponseDTO findById(java.util.UUID id) {
        UnavailableDate entity = unavailableDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia indisponível não encontrado"));
        return UnavailableDateResponseDTO.fromEntity(entity);
    }

    public UnavailableDateResponseDTO update(java.util.UUID id, UnavailableDateRequestDTO dto) {
        validateDate(dto.date());

        UnavailableDate existing = unavailableDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia indisponível não encontrado"));

        unavailableDateRepository.findByDate(dto.date())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new RuntimeException("Já existe um dia indisponível cadastrado para esta data");
                });

        existing.setDate(dto.date());
        existing.setReason(dto.reason());

        UnavailableDate updated = unavailableDateRepository.save(existing);
        return UnavailableDateResponseDTO.fromEntity(updated);
    }

    public void delete(java.util.UUID id) {
        if (!unavailableDateRepository.existsById(id)) {
            throw new RuntimeException("Dia indisponível não encontrado");
        }
        unavailableDateRepository.deleteById(id);
    }

    public Set<LocalDate> getUnavailableDatesBetween(LocalDate startDate, LocalDate endDate) {
        return unavailableDateRepository.findByDateBetween(startDate, endDate).stream()
                .map(UnavailableDate::getDate)
                .collect(Collectors.toSet());
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Data é obrigatória");
        }
    }
}
