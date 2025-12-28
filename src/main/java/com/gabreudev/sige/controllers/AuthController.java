package com.gabreudev.sige.controllers;

import com.gabreudev.sige.entities.user.*;
import com.gabreudev.sige.entities.user.dto.AuthenticationDTO;
import com.gabreudev.sige.entities.user.dto.LoginResponseDTO;
import com.gabreudev.sige.infra.SecurityConfigurations;
import com.gabreudev.sige.infra.TokenService;
import com.gabreudev.sige.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name = "Endpoints de usuario")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data){
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("student/register")
    public ResponseEntity registerStudent(@RequestBody @Valid UserRegisterDTO data){
        try{
            userService.registerStudent(data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cadastro falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("admin/register")
    public ResponseEntity registerAdmin(@RequestBody @Valid UserRegisterDTO data){
        try{
            userService.registerAdmin(data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cadastro falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("supervisor/register")
    public ResponseEntity registerSupervisor(@RequestBody @Valid UserRegisterDTO data){
        try{
            userService.registerSupervisor(data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cadastro falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("preceptor/register")
    public ResponseEntity registerPreceptor(@RequestBody @Valid UserRegisterDTO data){
        try{
            userService.registerPreceptor(data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cadastro falhou: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("user-authenticated")
    public ResponseEntity getUserAuthenticated(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(user);
    }
}