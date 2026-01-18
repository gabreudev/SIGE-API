package com.gabreudev.sige.controllers;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.unity.UnityRole;
import com.gabreudev.sige.entities.unity.dto.*;
import com.gabreudev.sige.entities.unity.dto.*;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.services.UnityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unities")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UnityController {

    @Autowired
    private UnityService unityService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnityResponseDTO> createUnity(@RequestBody UnityCreateDTO dto) {
        Unity unity = unityService.createUnity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UnityResponseDTO(unity));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PRECEPTOR', 'STUDENT')")
    public ResponseEntity<List<UnityResponseDTO>> getAllUnities() {
        List<Unity> unities = unityService.findAll();
        List<UnityResponseDTO> response = unities.stream()
                .map(UnityResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{unityRole}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PRECEPTOR', 'STUDENT')")
    public ResponseEntity<List<UnityResponseDTO>> getUnitiesByRole(@PathVariable UnityRole unityRole) {
        List<Unity> unities = unityService.findByUnityRole(unityRole);
        List<UnityResponseDTO> response = unities.stream()
                .map(UnityResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PRECEPTOR', 'STUDENT')")
    public ResponseEntity<UnityResponseDTO> getUnityById(@PathVariable UUID id) {
        Unity unity = unityService.findById(id);
        return ResponseEntity.ok(new UnityResponseDTO(unity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnityResponseDTO> updateUnity(@PathVariable UUID id, @RequestBody UnityUpdateDTO dto) {
        Unity unity = unityService.updateUnity(id, dto);
        return ResponseEntity.ok(new UnityResponseDTO(unity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUnity(@PathVariable UUID id) {
        unityService.deleteUnity(id);
        return ResponseEntity.noContent().build();
    }
}

