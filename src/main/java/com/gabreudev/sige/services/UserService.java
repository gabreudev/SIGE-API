package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRegisterDTO;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.entities.user.dto.*;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UnityRepository unityRepository;

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            password.append(random.nextInt(10));
        }
        return password.toString();
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    private User registerUser(UserRegisterDTO data, UserRole userRole, String password, boolean generatePassword) {
        if (userExists(data.username())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        String actualPassword = generatePassword ? generateRandomPassword() : password;
        String encryptedPassword = passwordEncoder.encode(actualPassword);

        User newUser = new User(data, encryptedPassword, userRole);

        log.info("Registrando {}: {}{}", userRole.name().toLowerCase(),
                data.username(),
                generatePassword ? " com senha gerada: " + actualPassword : "");

        User savedUser = userRepository.save(newUser);

        // Enviar email com a senha (se senha foi gerada)
        if (generatePassword) {
            //mailService.sendPasswordEmail(data.email(), data.username(), actualPassword, userRole.name());
        }

        return savedUser;
    }

    public User registerStudent(UserRegisterDTO data) {
        return registerUser(data, UserRole.STUDENT, null, true);
    }

    public User registerSupervisor(UserRegisterDTO data) {
        return registerUser(data, UserRole.SUPERVISOR, null, true);
    }

    public User registerPreceptor(UserRegisterDTO data) {
        return registerUser(data, UserRole.PRECEPTOR, null, true);
    }

    public User registerAdmin(UserRegisterDTO data) {
        return registerUser(data, UserRole.ADMIN, data.password(), false);
    }

    public List<User> findUsersByRole(UserRole userRole) {
        return userRepository.findByUserRole(userRole);
    }

    public List<UserResponseDTO> findUsersByRoleWithPreferences(UserRole userRole) {
        List<User> users = userRepository.findByUserRole(userRole);

        return users.stream()
                .map(user -> {
                    // Só busca unidades preferidas se for STUDENT
                    List<Unity> preferredUnities = List.of();
                    if (userRole == UserRole.STUDENT) {
                        preferredUnities = unityRepository.findAllById(user.getPreferredUnityIds());
                    }
                    return UserResponseDTO.fromUser(user, preferredUnities);
                })
                .collect(Collectors.toList());
    }

    private void checkUpdatePermission(UUID id, User userLogged) {
        if (!userLogged.getId().equals(id) && userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este usuário");
        }
    }

    public User updateStudent(UUID id, StudentUpdateDTO data, User userLogged) {
        checkUpdatePermission(id, userLogged);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
        }

        User updatedUser = new User(user, data);
        log.info("Atualizando estudante: {}", updatedUser.getUsername());
        return userRepository.save(updatedUser);
    }

    public User updateSupervisor(UUID id, SupervisorUpdateDTO data, User userLogged) {
        checkUpdatePermission(id, userLogged);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
        }

        User updatedUser = new User(user, data);
        log.info("Atualizando supervisor: {}", updatedUser.getUsername());
        return userRepository.save(updatedUser);
    }

    public User updatePreceptor(UUID id, PreceptorUpdateDTO data, User userLogged) {
        checkUpdatePermission(id, userLogged);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
        }

        User updatedUser = new User(user, data);
        log.info("Atualizando preceptor: {}", updatedUser.getUsername());
        return userRepository.save(updatedUser);
    }

    public User updateAdmin(UUID id, AdminUpdateDTO data, User userLogged) {
        checkUpdatePermission(id, userLogged);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
        }

        String encryptedPassword = null;
        if (data.password() != null && !data.password().isEmpty()) {
            encryptedPassword = passwordEncoder.encode(data.password());
        }

        User updatedUser = new User(user, data, encryptedPassword);
        log.info("Atualizando admin: {}", updatedUser.getUsername());
        return userRepository.save(updatedUser);
    }

    public void deleteUser(UUID id, User userLogged) {
        checkUpdatePermission(id, userLogged);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        log.info("Deletando usuário: {} ({})", user.getUsername(), user.getUserRole());
        userRepository.delete(user);
    }
}
