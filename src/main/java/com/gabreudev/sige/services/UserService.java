package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRegisterDTO;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.entities.user.dto.AdminUpdateDTO;
import com.gabreudev.sige.entities.user.dto.PreceptorUpdateDTO;
import com.gabreudev.sige.entities.user.dto.StudentUpdateDTO;
import com.gabreudev.sige.entities.user.dto.SupervisorUpdateDTO;
import com.gabreudev.sige.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
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

    public User registerStudent(UserRegisterDTO data) {
        if (userExists(data.username())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        String randomPassword = generateRandomPassword();
        String encryptedPassword = passwordEncoder.encode(randomPassword);

        User newUser = new User(
            data.username(),
            data.email(),
            encryptedPassword,
            UserRole.STUDENT,
            true,
            data.coren(),
            data.registration()
        );
        newUser.setInternshipRole(data.internshipRole());

        log.info("Registrando estudante: {} com senha gerada: {}", data.username(), randomPassword);
        User savedUser = userRepository.save(newUser);

        // Enviar email com a senha
        //mailService.sendPasswordEmail(data.email(), data.username(), randomPassword, "Estudante");

        return savedUser;
    }

    public User registerSupervisor(UserRegisterDTO data) {
        if (userExists(data.username())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        String randomPassword = generateRandomPassword();
        String encryptedPassword = passwordEncoder.encode(randomPassword);

        User newUser = new User(
            data.username(),
            data.email(),
            encryptedPassword,
            UserRole.SUPERVISOR,
            true,
            data.coren(),
            data.registration()
        );
        newUser.setInternshipRole(data.internshipRole());

        log.info("Registrando supervisor: {} com senha gerada: {}", data.username(), randomPassword);
        User savedUser = userRepository.save(newUser);

        // Enviar email com a senha
        //mailService.sendPasswordEmail(data.email(), data.username(), randomPassword, "Supervisor");

        return savedUser;
    }

    public User registerPreceptor(UserRegisterDTO data) {
        if (userExists(data.username())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        String randomPassword = generateRandomPassword();
        String encryptedPassword = passwordEncoder.encode(randomPassword);

        User newUser = new User(
            data.username(),
            data.email(),
            encryptedPassword,
            UserRole.PRECEPTOR,
            true,
            data.coren(),
            data.registration()
        );
        newUser.setInternshipRole(data.internshipRole());

        log.info("Registrando preceptor: {} com senha gerada: {}", data.username(), randomPassword);
        User savedUser = userRepository.save(newUser);

        // Enviar email com a senha
        //mailService.sendPasswordEmail(data.email(), data.username(), randomPassword, "Preceptor");

        return savedUser;
    }

    public User registerAdmin(UserRegisterDTO data) {
        if (userExists(data.username())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = new User(
            data.username(),
            data.email(),
            encryptedPassword,
            UserRole.ADMIN,
            true,
            data.coren(),
            data.registration()
        );
        newUser.setInternshipRole(data.internshipRole());

        log.info("Registrando admin: {}", data.username());
        return userRepository.save(newUser);
    }

    public List<User> findUsersByRole(UserRole userRole) {
        return userRepository.findByUserRole(userRole);
    }

    public User updateStudent(UUID id, StudentUpdateDTO data, @AuthenticationPrincipal User userLogged) {
        if (!userLogged.getId().equals(id) && userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este estudante");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));


        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
            user.setUsername(data.username());
        }

        if (data.email() != null) {
            user.setEmail(data.email());
        }

        if (data.registration() != null) {
            user.setRegistration(data.registration());
        }

        if (data.internshipRole() != null) {
            user.setInternshipRole(data.internshipRole());
        }

        if (data.enabled() != null) {
            user.setEnabled(data.enabled());
        }

        log.info("Atualizando estudante: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User updateSupervisor(UUID id, SupervisorUpdateDTO data, User userLogged) {
        if (!userLogged.getId().equals(id) || userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este supervisor");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
            user.setUsername(data.username());
        }

        if (data.email() != null) {
            user.setEmail(data.email());
        }

        if (data.coren() != null) {
            user.setCoren(data.coren());
        }


        if (data.enabled() != null) {
            user.setEnabled(data.enabled());
        }

        log.info("Atualizando supervisor: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User updatePreceptor(UUID id, PreceptorUpdateDTO data, @AuthenticationPrincipal User userLogged) {
        if (!userLogged.getId().equals(id) && userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este preceptor");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
            user.setUsername(data.username());
        }

        if (data.email() != null) {
            user.setEmail(data.email());
        }

        if (data.enabled() != null) {
            user.setEnabled(data.enabled());
        }

        log.info("Atualizando preceptor: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User updateAdmin(UUID id, AdminUpdateDTO data, User userLogged) {
        if (!userLogged.getId().equals(id) && userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este admin");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (data.username() != null && !data.username().equals(user.getUsername())) {
            if (userExists(data.username())) {
                throw new IllegalArgumentException("Username já existe");
            }
            user.setUsername(data.username());
        }

        if (data.email() != null) {
            user.setEmail(data.email());
        }

        if (data.password() != null && !data.password().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(data.password());
            user.setPassword(encryptedPassword);
        }

        if (data.enabled() != null) {
            user.setEnabled(data.enabled());
        }

        log.info("Atualizando admin: {}", user.getUsername());
        return userRepository.save(user);
    }

    public void deleteUser(UUID id, User userLogged) {
        if (!userLogged.getId().equals(id) && userLogged.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Usuário não tem permissão para deletar este usuário");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        log.info("Deletando usuário: {} ({})", user.getUsername(), user.getUserRole());
        userRepository.delete(user);
    }
}
