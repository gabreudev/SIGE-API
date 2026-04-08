package com.gabreudev.sige.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabreudev.sige.entities.calendar.dto.UnavailableDateRequestDTO;
import com.gabreudev.sige.entities.calendar.dto.UnavailableDateResponseDTO;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.services.UnavailableDateService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("unavailable-dates")
@RequiredArgsConstructor
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UnavailableDateController {

    private final UnavailableDateService unavailableDateService;

    @PostMapping
    public ResponseEntity<UnavailableDateResponseDTO> create(@RequestBody UnavailableDateRequestDTO dto) {
        UnavailableDateResponseDTO created = unavailableDateService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<UnavailableDateResponseDTO>> findAll() {
        return ResponseEntity.ok(unavailableDateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnavailableDateResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(unavailableDateService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnavailableDateResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UnavailableDateRequestDTO dto) {
        return ResponseEntity.ok(unavailableDateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        unavailableDateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
