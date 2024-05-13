package com.github.thiagomarqs.gerenciamentopessoas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "API de Gerenciamento de Pessoas",
				description = "Uma API REST para gerenciamento de pessoas que permite o cadastro, edição e consulta de pessoas e seus endereços.",
				contact = @Contact(
						name = "Thiago Damasceno",
						url = "https://github.com/thiagomarqs"
				)
		)
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
