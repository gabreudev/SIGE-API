package com.gabreudev.sige;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.unity.UnityRole;
import com.gabreudev.sige.entities.user.InternshipRole;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRegisterDTO;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
@OpenAPIDefinition(info = @Info(title = "Swagger OpenApi", version = "1", description = "API desenvolvida para a versão beta do Sistema de Gestão de Estágios - SIGE"))
public class SigeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SigeApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, UnityRepository unityRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Verifica se já existem dados para não duplicar
			if (userRepository.count() > 0) {
				log.info("Banco de dados já possui dados. Pulando inicialização.");
				return;
			}

			log.info("Iniciando criação de dados de exemplo...");

			// 1. Criar Admin
			UserRegisterDTO adminDTO = new UserRegisterDTO(
					"admin",
					"Administrador do Sistema",
					"admin@sige.com",
					"admin123",
					null,
					UserRole.ADMIN,
					null,
					true
			);
			User admin = new User(adminDTO, passwordEncoder.encode("admin123"), UserRole.ADMIN);
			admin = userRepository.save(admin);
			log.info("✓ Admin criado - username: admin, senha: admin123");

			// 2. Criar Preceptor
			UserRegisterDTO preceptorDTO = new UserRegisterDTO(
					"preceptor1",
					"Dr. João Silva",
					"joao.silva@sige.com",
					"preceptor123",
					"COREM12345",
					UserRole.PRECEPTOR,
					null,
					true
			);
			User preceptor = new User(preceptorDTO, passwordEncoder.encode("preceptor123"), UserRole.PRECEPTOR);
			preceptor = userRepository.save(preceptor);
			log.info("✓ Preceptor criado - username: preceptor1, senha: preceptor123");

			// 3. Criar Unidade
			Map<String, Object> availability = new HashMap<>();
			availability.put("totalSlots", 10);
			availability.put("occupiedSlots", 2);
			availability.put("availableSlots", 8);

			Unity unity = new Unity(
					"Hospital Central",
					"Rua Principal, 123 - Centro",
					UnityRole.HOSPITAL,
					preceptor,
					availability
			);
			unity = unityRepository.save(unity);
			log.info("✓ Unidade criada - nome: Hospital Central, tipo: HOSPITAL");

			// 4. Criar outra Unidade (UBS)
			Map<String, Object> availability2 = new HashMap<>();
			availability2.put("totalSlots", 5);
			availability2.put("occupiedSlots", 1);
			availability2.put("availableSlots", 4);

			Unity unity2 = new Unity(
					"UBS Vila Nova",
					"Av. Secundária, 456 - Bairro Vila Nova",
					UnityRole.UBS,
					preceptor,
					availability2
			);
			unity2 = unityRepository.save(unity2);
			log.info("✓ Unidade criada - nome: UBS Vila Nova, tipo: UBS");

			// 5. Criar Supervisor
			UserRegisterDTO supervisorDTO = new UserRegisterDTO(
					"supervisor1",
					"Profa. Maria Santos",
					"maria.santos@sige.com",
					"supervisor123",
					"SIAPE98765",
					UserRole.SUPERVISOR,
					null,
					false
			);
			User supervisor = new User(supervisorDTO, passwordEncoder.encode("supervisor123"), UserRole.SUPERVISOR);
			supervisor = userRepository.save(supervisor);
			log.info("✓ Supervisor criado - username: supervisor1, senha: supervisor123");

			// 6. Criar Estudante
			UserRegisterDTO studentDTO = new UserRegisterDTO(
					"student1",
					"Carlos Eduardo Oliveira",
					"carlos.oliveira@sige.com",
					"student123",
					"20230001",
					UserRole.STUDENT,
					InternshipRole.FIRST,
					true
			);
			User student = new User(studentDTO, passwordEncoder.encode("student123"), UserRole.STUDENT);
			student = userRepository.save(student);
			log.info("✓ Estudante criado - username: student1, senha: student123");

			log.info("========================================");
			log.info("Dados de exemplo criados com sucesso!");
			log.info("========================================");
			log.info("CREDENCIAIS DE ACESSO:");
			log.info("Admin      - username: admin        | senha: admin123");
			log.info("Preceptor  - username: preceptor1   | senha: preceptor123");
			log.info("Supervisor - username: supervisor1  | senha: supervisor123");
			log.info("Estudante  - username: student1     | senha: student123");
			log.info("========================================");
		};
	}

}
