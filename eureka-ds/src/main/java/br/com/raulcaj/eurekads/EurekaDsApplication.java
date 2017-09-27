package br.com.raulcaj.eurekads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaDsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaDsApplication.class, args);
	}
}
