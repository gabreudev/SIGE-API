package com.gabreudev.sige.controllers;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.entities.user.dto.*;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.services.UserService;
import com.gabreudev.sige.services.UserUnityPreferenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@Tag(name = "Endpoints de usuários")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserUnityPreferenceService preferenceService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("role/{role}")
    public ResponseEntity getUsersByRole(@PathVariable String role){
        try{
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            var users = userService.findUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Role inválida: " + role);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("student/update/{id}")
    public ResponseEntity updateStudent(@PathVariable UUID id, @RequestBody @Valid StudentUpdateDTO data, @AuthenticationPrincipal User user){
        try{
            User updatedUser = userService.updateStudent(id, data, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Atualização falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("admin/update/{id}")
    public ResponseEntity updateAdmin(@PathVariable UUID id, @RequestBody @Valid AdminUpdateDTO data, @AuthenticationPrincipal User user){
        try{
            User updatedUser = userService.updateAdmin(id, data, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Atualização falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("supervisor/update/{id}")
    public ResponseEntity updateSupervisor(@PathVariable UUID id, @RequestBody @Valid SupervisorUpdateDTO data, @AuthenticationPrincipal User user){
        try{
            User updatedUser = userService.updateSupervisor(id, data, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Atualização falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("preceptor/update/{id}")
    public ResponseEntity updatePreceptor(@PathVariable UUID id, @RequestBody @Valid PreceptorUpdateDTO data, @AuthenticationPrincipal User user){
        try{
            User updatedUser = userService.updatePreceptor(id, data, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Atualização falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteUser(@PathVariable UUID id, @AuthenticationPrincipal User user){
        try{
            userService.deleteUser(id, user);
            return ResponseEntity.ok().body("Usuário deletado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar usuário: " + e.getMessage());
        }
    }

    // Rotas de Preferências de Unidades
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("{userId}/preferred-unities")
    public ResponseEntity setPreferredUnities(
            @PathVariable UUID userId,
            @RequestBody @Valid UserPreferredUnitiesDTO data,
            @AuthenticationPrincipal User user){
        try{
            // Verifica se o usuário pode modificar essas preferências
            if (!user.getId().equals(userId) && !user.getUserRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para modificar essas preferências");
            }

            User updatedUser = preferenceService.setPreferredUnities(userId, data.unityIds());
            List<Unity> unities = preferenceService.getPreferredUnities(userId);
            return ResponseEntity.ok(UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao definir preferências: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("{userId}/preferred-unities/{unityId}")
    public ResponseEntity addPreferredUnity(
            @PathVariable UUID userId,
            @PathVariable UUID unityId,
            @AuthenticationPrincipal User user){
        try{
            if (!user.getId().equals(userId) && !user.getUserRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para modificar essas preferências");
            }

            User updatedUser = preferenceService.addPreferredUnity(userId, unityId);
            List<Unity> unities = preferenceService.getPreferredUnities(userId);
            return ResponseEntity.ok(UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar preferência: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("{userId}/preferred-unities/{unityId}")
    public ResponseEntity removePreferredUnity(
            @PathVariable UUID userId,
            @PathVariable UUID unityId,
            @AuthenticationPrincipal User user){
        try{
            // Verifica se o usuário pode modificar essas preferências
            if (!user.getId().equals(userId) && !user.getUserRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para modificar essas preferências");
            }

            User updatedUser = preferenceService.removePreferredUnity(userId, unityId);
            List<Unity> unities = preferenceService.getPreferredUnities(userId);
            return ResponseEntity.ok(UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover preferência: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("{userId}/preferred-unities")
    public ResponseEntity getPreferredUnities(@PathVariable UUID userId){
        try{
            User user = userService.findUsersByRole(null).stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Unity> unities = preferenceService.getPreferredUnities(userId);
            return ResponseEntity.ok(UserPreferredUnitiesResponseDTO.fromUser(user, unities));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar preferências: " + e.getMessage());
        }
    }
}
