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

			log.info("Iniciando criação de dados...");

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
			log.info("✓ Admin criado");

			// 2. Criar Preceptor padrão
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
			log.info("✓ Preceptor criado");

			// 3. Criar Supervisor
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
			log.info("✓ Supervisor criado");

			// ========== CRIAR UBS ==========

			// UBS Ipueiras II – Tarde
			Map<String, Object> availIpueiras = new HashMap<>();
			availIpueiras.put("monday", Map.of("morning", false, "afternoon", true));
			availIpueiras.put("tuesday", Map.of("morning", false, "afternoon", true));
			availIpueiras.put("wednesday", Map.of("morning", false, "afternoon", true));
			availIpueiras.put("thursday", Map.of("morning", false, "afternoon", true));
			availIpueiras.put("friday", Map.of("morning", false, "afternoon", true));
			Unity ubsIpueiras = new Unity("UBS Ipueiras II", "Ipueiras II", UnityRole.UBS, preceptor, 2, availIpueiras);
			ubsIpueiras = unityRepository.save(ubsIpueiras);
			log.info("✓ UBS Ipueiras II criada");

			// UBS Belinha Nunes II – Tarde
			Map<String, Object> availBelinhaNunes2 = new HashMap<>();
			availBelinhaNunes2.put("monday", Map.of("morning", false, "afternoon", true));
			availBelinhaNunes2.put("tuesday", Map.of("morning", false, "afternoon", true));
			availBelinhaNunes2.put("wednesday", Map.of("morning", false, "afternoon", true));
			availBelinhaNunes2.put("thursday", Map.of("morning", false, "afternoon", true));
			availBelinhaNunes2.put("friday", Map.of("morning", false, "afternoon", true));
			Unity ubsBelinhaNunes2 = new Unity("UBS Belinha Nunes II", "Belinha Nunes II", UnityRole.UBS, preceptor, 2, availBelinhaNunes2);
			ubsBelinhaNunes2 = unityRepository.save(ubsBelinhaNunes2);
			log.info("✓ UBS Belinha Nunes II criada");

			// UBS Parque de Exposição – Manhã
			Map<String, Object> availParqueExposicao = new HashMap<>();
			availParqueExposicao.put("monday", Map.of("morning", true, "afternoon", false));
			availParqueExposicao.put("tuesday", Map.of("morning", true, "afternoon", false));
			availParqueExposicao.put("wednesday", Map.of("morning", true, "afternoon", false));
			availParqueExposicao.put("thursday", Map.of("morning", true, "afternoon", false));
			availParqueExposicao.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsParqueExposicao = new Unity("UBS Parque de Exposição", "Parque de Exposição", UnityRole.UBS, preceptor, 2, availParqueExposicao);
			ubsParqueExposicao = unityRepository.save(ubsParqueExposicao);
			log.info("✓ UBS Parque de Exposição criada");

			// UBS Catavento – Manhã
			Map<String, Object> availCatavento = new HashMap<>();
			availCatavento.put("monday", Map.of("morning", true, "afternoon", false));
			availCatavento.put("tuesday", Map.of("morning", true, "afternoon", false));
			availCatavento.put("wednesday", Map.of("morning", true, "afternoon", false));
			availCatavento.put("thursday", Map.of("morning", true, "afternoon", false));
			availCatavento.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsCatavento = new Unity("UBS Catavento", "Catavento", UnityRole.UBS, preceptor, 2, availCatavento);
			ubsCatavento = unityRepository.save(ubsCatavento);
			log.info("✓ UBS Catavento criada");

			// UBS Belinha Nunes I – Manhã
			Map<String, Object> availBelinhaNunes1 = new HashMap<>();
			availBelinhaNunes1.put("monday", Map.of("morning", true, "afternoon", false));
			availBelinhaNunes1.put("tuesday", Map.of("morning", true, "afternoon", false));
			availBelinhaNunes1.put("wednesday", Map.of("morning", true, "afternoon", false));
			availBelinhaNunes1.put("thursday", Map.of("morning", true, "afternoon", false));
			availBelinhaNunes1.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsBelinhaNunes1 = new Unity("UBS Belinha Nunes I", "Belinha Nunes I", UnityRole.UBS, preceptor, 1, availBelinhaNunes1);
			ubsBelinhaNunes1 = unityRepository.save(ubsBelinhaNunes1);
			log.info("✓ UBS Belinha Nunes I criada");

			// UBS Pantanal – Tarde
			Map<String, Object> availPantanal = new HashMap<>();
			availPantanal.put("monday", Map.of("morning", false, "afternoon", true));
			availPantanal.put("tuesday", Map.of("morning", false, "afternoon", true));
			availPantanal.put("wednesday", Map.of("morning", false, "afternoon", true));
			availPantanal.put("thursday", Map.of("morning", false, "afternoon", true));
			availPantanal.put("friday", Map.of("morning", false, "afternoon", true));
			Unity ubsPantanal = new Unity("UBS Pantanal", "Pantanal", UnityRole.UBS, preceptor, 2, availPantanal);
			ubsPantanal = unityRepository.save(ubsPantanal);
			log.info("✓ UBS Pantanal criada");

			// UBS Conduru – Manhã
			Map<String, Object> availConduru = new HashMap<>();
			availConduru.put("monday", Map.of("morning", true, "afternoon", false));
			availConduru.put("tuesday", Map.of("morning", true, "afternoon", false));
			availConduru.put("wednesday", Map.of("morning", true, "afternoon", false));
			availConduru.put("thursday", Map.of("morning", true, "afternoon", false));
			availConduru.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsConduru = new Unity("UBS Conduru", "Conduru", UnityRole.UBS, preceptor, 2, availConduru);
			ubsConduru = unityRepository.save(ubsConduru);
			log.info("✓ UBS Conduru criada");

			// UBS Cecília Nery – Manhã
			Map<String, Object> availCeciliaNery = new HashMap<>();
			availCeciliaNery.put("monday", Map.of("morning", true, "afternoon", false));
			availCeciliaNery.put("tuesday", Map.of("morning", true, "afternoon", false));
			availCeciliaNery.put("wednesday", Map.of("morning", true, "afternoon", false));
			availCeciliaNery.put("thursday", Map.of("morning", true, "afternoon", false));
			availCeciliaNery.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsCeciliaNery = new Unity("UBS Cecília Nery", "Cecília Nery", UnityRole.UBS, preceptor, 2, availCeciliaNery);
			ubsCeciliaNery = unityRepository.save(ubsCeciliaNery);
			log.info("✓ UBS Cecília Nery criada");

			// UBS Belo Norte – Manhã
			Map<String, Object> availBeloNorte = new HashMap<>();
			availBeloNorte.put("monday", Map.of("morning", true, "afternoon", false));
			availBeloNorte.put("tuesday", Map.of("morning", true, "afternoon", false));
			availBeloNorte.put("wednesday", Map.of("morning", true, "afternoon", false));
			availBeloNorte.put("thursday", Map.of("morning", true, "afternoon", false));
			availBeloNorte.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsBeloNorte = new Unity("UBS Belo Norte", "Belo Norte", UnityRole.UBS, preceptor, 2, availBeloNorte);
			ubsBeloNorte = unityRepository.save(ubsBeloNorte);
			log.info("✓ UBS Belo Norte criada");

			// UBS Boa Sorte – Manhã
			Map<String, Object> availBoaSorte = new HashMap<>();
			availBoaSorte.put("monday", Map.of("morning", true, "afternoon", false));
			availBoaSorte.put("tuesday", Map.of("morning", true, "afternoon", false));
			availBoaSorte.put("wednesday", Map.of("morning", true, "afternoon", false));
			availBoaSorte.put("thursday", Map.of("morning", true, "afternoon", false));
			availBoaSorte.put("friday", Map.of("morning", true, "afternoon", false));
			Unity ubsBoaSorte = new Unity("UBS Boa Sorte", "Boa Sorte", UnityRole.UBS, preceptor, 2, availBoaSorte);
			ubsBoaSorte = unityRepository.save(ubsBoaSorte);
			log.info("✓ UBS Boa Sorte criada");

			// ========== CRIAR ALUNOS ==========

			// Alunos da UBS Ipueiras II
			createStudent(userRepository, passwordEncoder, "Sandy Pacheco", "sandy.pacheco", "20240001", false);
			createStudent(userRepository, passwordEncoder, "Mariana Martins", "mariana.martins", "20240002", false);

			// Alunos da UBS Belinha Nunes II
			createStudent(userRepository, passwordEncoder, "Mara Walklecia Veloso", "mara.veloso", "20240003", false);
			createStudent(userRepository, passwordEncoder, "Wyllyana Morais", "wyllyana.morais", "20240004", false);

			// Alunos da UBS Parque de Exposição
			createStudent(userRepository, passwordEncoder, "Arielly Tavares", "arielly.tavares", "20240005", false);
			createStudent(userRepository, passwordEncoder, "Jamilly Silva", "jamilly.silva", "20240006", false);

			// Alunos da UBS Catavento
			createStudent(userRepository, passwordEncoder, "Larissa Sousa", "larissa.sousa", "20240007", false);
			createStudent(userRepository, passwordEncoder, "Maria Fernanda Carvalho", "maria.carvalho", "20240008", false);

			// Alunos da UBS Belinha Nunes I
			createStudent(userRepository, passwordEncoder, "Larissa Silva", "larissa.silva", "20240009", false);

			// Alunos da UBS Pantanal
			createStudent(userRepository, passwordEncoder, "Raniel Costa", "raniel.costa", "20240010", true);
			createStudent(userRepository, passwordEncoder, "Sara Félix", "sara.felix", "20240011", false);

			// Alunos da UBS Conduru
			createStudent(userRepository, passwordEncoder, "Maria Lara Borges", "maria.borges", "20240012", false);
			createStudent(userRepository, passwordEncoder, "Elanha Araújo", "elanha.araujo", "20240013", false);

			// Alunos da UBS Cecília Nery
			createStudent(userRepository, passwordEncoder, "Ana Lívia Lima", "ana.lima", "20240014", false);
			createStudent(userRepository, passwordEncoder, "Cauã Couto", "caua.couto", "20240015", true);

			// Alunos da UBS Belo Norte
			createStudent(userRepository, passwordEncoder, "Marcos Gonçalves", "marcos.goncalves", "20240016", true);
			createStudent(userRepository, passwordEncoder, "Chíntia Bezerra", "chintia.bezerra", "20240017", false);

			// Alunos da UBS Boa Sorte
			createStudent(userRepository, passwordEncoder, "Sarah Ramila", "sarah.ramila", "20240018", false);
			createStudent(userRepository, passwordEncoder, "Naeli Lopes", "naeli.lopes", "20240019", false);

			log.info("========================================");
			log.info("✓ 10 UBS criadas com sucesso!");
			log.info("✓ 19 Alunos criados com sucesso!");
			log.info("========================================");
			log.info("CREDENCIAIS DE ACESSO:");
			log.info("Admin      - username: admin        | senha: admin123");
			log.info("Preceptor  - username: preceptor1   | senha: preceptor123");
			log.info("Supervisor - username: supervisor1  | senha: supervisor123");
			log.info("Alunos     - username: [primeironome.sobrenome] | senha: senha123");
			log.info("========================================");
		};
	}

	private void createStudent(UserRepository userRepository, PasswordEncoder passwordEncoder,
	                           String name, String username, String registration, Boolean male) {
		UserRegisterDTO studentDTO = new UserRegisterDTO(
				username,
				name,
				username + "@sige.com",
				"senha123",
				registration,
				UserRole.STUDENT,
				InternshipRole.FIRST,
				male
		);
		User student = new User(studentDTO, passwordEncoder.encode("senha123"), UserRole.STUDENT);
		userRepository.save(student);
		log.info("✓ Aluno criado: " + name);
	}

}
