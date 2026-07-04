package com.engeman.imoveis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImoveisApplication {

	public static void main(String[] args) {
		System.out.println("### DEBUG SPRING_DATASOURCE_URL = [" + System.getenv("SPRING_DATASOURCE_URL") + "]");
		System.out.println("### DEBUG tamanho = " + (System.getenv("SPRING_DATASOURCE_URL") == null ? "null" : System.getenv("SPRING_DATASOURCE_URL").length()));
		SpringApplication.run(ImoveisApplication.class, args);
	}
}
