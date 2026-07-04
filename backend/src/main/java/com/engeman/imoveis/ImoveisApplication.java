package com.engeman.imoveis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImoveisApplication {

	public static void main(String[] args) {
		String url = System.getenv("SPRING_DATASOURCE_URL");
		String jwt = System.getenv("JWT_SECRET");
		System.out.println("### DEBUG SPRING_DATASOURCE_URL = [" + url + "]");
		System.out.println("### DEBUG tamanho = " + (url == null ? "null" : url.length()));
		System.out.println("### DEBUG JWT_SECRET = [" + jwt + "]");
		SpringApplication.run(ImoveisApplication.class, args);
	}
}
