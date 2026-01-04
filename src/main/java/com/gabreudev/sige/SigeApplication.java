package com.gabreudev.sige;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@OpenAPIDefinition(info = @Info(title = "Swagger OpenApi", version = "1", description = "API desenvolvida para a versão beta do Sistema de Gestão de Estágios - SIGE"))
public class SigeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SigeApplication.class, args);
	}

}
