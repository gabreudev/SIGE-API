package com.gabreudev.sige.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordEmail(String toEmail, String username, String password, String userType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Bem-vindo ao SIGE - Suas Credenciais de Acesso");

            String emailBody = String.format(
                "Olá %s,\n\n" +
                "Seu cadastro no sistema SIGE foi realizado com sucesso!\n\n" +
                "Tipo de usuário: %s\n" +
                "Usuário: %s\n" +
                "Senha temporária: %s\n\n" +
                "Por favor, faça login no sistema e altere sua senha no primeiro acesso.\n\n" +
                "Atenciosamente,\n" +
                "Equipe SIGE",
                username,
                userType,
                username,
                password
            );

            message.setText(emailBody);

            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", toEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Falha ao enviar email: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String username, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("SIGE - Redefinição de Senha");

            String emailBody = String.format(
                "Olá %s,\n\n" +
                "Sua senha foi redefinida com sucesso!\n\n" +
                "Nova senha temporária: %s\n\n" +
                "Por favor, faça login no sistema e altere sua senha.\n\n" +
                "Atenciosamente,\n" +
                "Equipe SIGE",
                username,
                newPassword
            );

            message.setText(emailBody);

            mailSender.send(message);
            log.info("Email de redefinição enviado com sucesso para: {}", toEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de redefinição para {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Falha ao enviar email: " + e.getMessage());
        }
    }
}

