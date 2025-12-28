package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRegisterDTO;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

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
}

